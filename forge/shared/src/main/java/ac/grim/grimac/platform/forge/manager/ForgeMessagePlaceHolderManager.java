package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.manager.MessagePlaceHolderManager;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.forge.player.AbstractForgePlatformPlayer;
import ac.grim.grimac.utils.reflection.ReflectionUtils;
import me.clip.placeholderapi.forge.ForgePlaceholderAPI;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeMessagePlaceHolderManager implements MessagePlaceHolderManager {
    public static final boolean hasPlaceholderAPI = ReflectionUtils.hasClass("me.clip.placeholderapi.forge.ForgePlaceholderAPI");

    @Override
    public @NotNull String replacePlaceholders(@Nullable PlatformPlayer player, @NotNull String string) {
        if (!hasPlaceholderAPI) return string;
        ServerPlayer nativePlayer = player instanceof AbstractForgePlatformPlayer forgePlayer
                ? forgePlayer.serverPlayer()
                : null;
        return ForgePlaceholderAPI.setPlaceholders(nativePlayer, string);
    }
}