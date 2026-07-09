package ac.grim.grimac.platform.forge.mc1201.compat;

import ac.grim.grimac.manager.compat.ExternalMovementState;
import ac.grim.grimac.manager.compat.ExternalMovementStateProvider;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ForgeTaczExternalMovementStateProvider implements ExternalMovementStateProvider {
    private final Method fromLivingEntity;
    private final Method getSynDrawCoolDown;
    private final Method getSynIsBolting;
    private final Method getSynReloadState;
    private final Method getSynAimingProgress;
    private final Method getSynIsAiming;
    private final Method getDataHolder;
    private final Field isCrawling;
    private final AtomicBoolean warned = new AtomicBoolean(false);

    private final Map<UUID, CachedState> playerCache = Collections.synchronizedMap(
            new LinkedHashMap<>(64, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<UUID, CachedState> eldest) {
                    return size() > 200;
                }
            }
    );

    public ForgeTaczExternalMovementStateProvider() {
        try {
            Class<?> operatorClass = Class.forName("com.tacz.guns.api.entity.IGunOperator");
            Class<?> dataHolderClass = Class.forName("com.tacz.guns.entity.shooter.ShooterDataHolder");

            this.fromLivingEntity = operatorClass.getMethod("fromLivingEntity", LivingEntity.class);
            this.getSynDrawCoolDown = operatorClass.getMethod("getSynDrawCoolDown");
            this.getSynIsBolting = operatorClass.getMethod("getSynIsBolting");
            this.getSynReloadState = operatorClass.getMethod("getSynReloadState");
            this.getSynAimingProgress = operatorClass.getMethod("getSynAimingProgress");
            this.getSynIsAiming = operatorClass.getMethod("getSynIsAiming");
            this.getDataHolder = operatorClass.getMethod("getDataHolder");

            this.isCrawling = dataHolderClass.getField("isCrawling");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("TACZ API classes are unavailable", e);
        }
    }

    @Override
    public @NotNull ExternalMovementState getState(@NotNull GrimPlayer player) {
        if (player.platformPlayer == null || !(player.platformPlayer.getNative() instanceof LivingEntity livingEntity)) {
            return ExternalMovementState.EMPTY;
        }

        int currentTick = player.getLastTransactionReceived();
        CachedState entry = playerCache.get(player.getUniqueId());
        if (entry != null && entry.tick == currentTick) {
            return entry.state;
        }

        try {
            Object operator = fromLivingEntity.invoke(null, livingEntity);
            Object reloadState = getSynReloadState.invoke(operator);
            Object stateType = reloadState.getClass().getMethod("getStateType").invoke(reloadState);

            boolean aiming = (boolean) getSynIsAiming.invoke(operator);
            float aimingProgress = (float) getSynAimingProgress.invoke(operator);
            boolean reloading = (boolean) stateType.getClass().getMethod("isReloading").invoke(stateType);
            boolean reloadFinishing = (boolean) stateType.getClass().getMethod("isReloadFinishing").invoke(stateType);
            boolean bolting = (boolean) getSynIsBolting.invoke(operator);
            boolean drawingGun = ((Number) getSynDrawCoolDown.invoke(operator)).longValue() > 0;
            Object dataHolder = getDataHolder.invoke(operator);
            boolean taczCrawling = (boolean) isCrawling.get(dataHolder);

            boolean gunActionActive = aiming || aimingProgress > 0.0F || reloading || bolting || drawingGun || taczCrawling;
            boolean sprintSuppressedByMod = aiming || (reloading && !reloadFinishing) || bolting || drawingGun || taczCrawling;

            ExternalMovementState state = new ExternalMovementState(
                    aiming,
                    reloading,
                    bolting,
                    drawingGun,
                    taczCrawling,
                    gunActionActive,
                    sprintSuppressedByMod
            );
            playerCache.put(player.getUniqueId(), new CachedState(currentTick, state));
            return state;
        } catch (ReflectiveOperationException | RuntimeException e) {
            warnOnce(e);
            return ExternalMovementState.EMPTY;
        }
    }

    private void warnOnce(Exception e) {
        if (warned.getAndSet(true)) return;
        LogUtil.warn("Failed to read TACZ movement state; TACZ compatibility will be inactive. " + e.getMessage());
    }

    private record CachedState(int tick, ExternalMovementState state) {}
}
