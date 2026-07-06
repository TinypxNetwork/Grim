package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.manager.MessagePlaceHolderManager;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.forge.player.AbstractForgePlatformPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.reflection.ReflectionUtils;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ForgeMessagePlaceHolderManager implements MessagePlaceHolderManager {
    public static final boolean hasPlaceholderAPI = ReflectionUtils.hasClass("me.clip.placeholderapi.forge.ForgePlaceholderAPI");
    private static Method setPlaceholdersMethod;
    private static boolean warned;

    @Override
    public @NotNull String replacePlaceholders(@Nullable PlatformPlayer player, @NotNull String string) {
        if (!hasPlaceholderAPI) return string;
        ServerPlayer nativePlayer = player instanceof AbstractForgePlatformPlayer forgePlayer
                ? forgePlayer.serverPlayer()
                : null;
        try {
            if (setPlaceholdersMethod == null) {
                Class<?> apiClass = Class.forName("me.clip.placeholderapi.forge.ForgePlaceholderAPI");
                setPlaceholdersMethod = apiClass.getMethod("setPlaceholders", ServerPlayer.class, String.class);
            }
            Object result = setPlaceholdersMethod.invoke(null, nativePlayer, string);
            return result instanceof String replaced ? replaced : string;
        } catch (ReflectiveOperationException | LinkageError e) {
            if (!warned) {
                warned = true;
                LogUtil.warn("Failed to call Forge PlaceholderAPI: " + e.getMessage());
            }
            return string;
        }
    }
}
