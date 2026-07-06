package ac.grim.grimac.platform.forge.player;

import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.player.AbstractPlatformPlayerFactory;
import ac.grim.grimac.platform.api.player.OfflinePlatformPlayer;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class ForgePlayerFactory extends AbstractPlatformPlayerFactory<ServerPlayer> {

    private final Map<UUID, OfflinePlatformPlayer> offlinePlatformPlayerCache = new HashMap<>();
    private final Function<ServerPlayer, AbstractForgePlatformPlayer> getPlayerFunction;
    private final Function<Object, GrimEntity> getEntityFunction;
    private final Function<ServerPlayer, ForgePlatformInventory> getPlayerInventoryFunction;

    public ForgePlayerFactory(
            Function<ServerPlayer, AbstractForgePlatformPlayer> getPlayerFunction,
            Function<Object, GrimEntity> getEntityFunction,
            Function<ServerPlayer, ForgePlatformInventory> getPlayerInventoryFunction
    ) {
        this.getPlayerFunction = getPlayerFunction;
        this.getEntityFunction = getEntityFunction;
        this.getPlayerInventoryFunction = getPlayerInventoryFunction;
    }

    @Override
    protected ServerPlayer getNativePlayer(@NotNull UUID uuid) {
        MinecraftServer server = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER;
        return server.getPlayerList().getPlayer(uuid);
    }

    @Override
    protected ServerPlayer getNativePlayer(@NotNull String name) {
        MinecraftServer server = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER;
        return server.getPlayerList().getPlayerByName(name);
    }

    @Override
    protected PlatformPlayer createPlatformPlayer(@NotNull ServerPlayer nativePlayer) {
        return getPlayerFunction.apply(nativePlayer);
    }

    @Override
    protected UUID getPlayerUUID(@NotNull ServerPlayer nativePlayer) {
        return nativePlayer.getUUID();
    }

    @Override
    protected Collection<ServerPlayer> getNativeOnlinePlayers() {
        return new ArrayList<>(AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getPlayerList().getPlayers());
    }

    @Override
    public OfflinePlatformPlayer getOfflineFromUUID(@NotNull UUID uuid) {
        OfflinePlatformPlayer result = this.getFromUUID(uuid);
        if (result == null) {
            result = this.offlinePlatformPlayerCache.get(uuid);
            if (result == null) {
                result = new ForgeOfflinePlatformPlayer(uuid, "");
                this.offlinePlatformPlayerCache.put(uuid, result);
            }
        } else {
            this.offlinePlatformPlayerCache.remove(uuid);
        }
        return result;
    }

    @Override
    public OfflinePlatformPlayer getOfflineFromName(@NotNull String name) {
        OfflinePlatformPlayer result = this.getFromName(name);
        if (result == null) {
            result = new ForgeOfflinePlatformPlayer(
                    UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)),
                    name
            );
        }
        return result;
    }

    @Override
    public Collection<OfflinePlatformPlayer> getOfflinePlayers() {
        Collection<OfflinePlatformPlayer> players = new HashSet<>();
        for (ServerPlayer player : AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getPlayerList().getPlayers()) {
            players.add(this.getOfflineFromUUID(player.getUUID()));
        }
        return players;
    }

    public ForgePlatformInventory getPlatformInventory(ServerPlayer player) {
        return getPlayerInventoryFunction.apply(player);
    }

    public GrimEntity getPlatformEntity(Object entity) {
        return getEntityFunction.apply(entity);
    }
}