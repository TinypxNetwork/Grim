package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.manager.ItemResetHandler;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.forge.ForgeConversionUtil;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ForgeItemResetHandler implements ItemResetHandler {

    @Override
    public void resetItemUsage(@Nullable PlatformPlayer player) {
        if (nativePlayer(player) instanceof Player forgePlayer) {
            forgePlayer.stopUsingItem();
        }
    }

    @Override
    public InteractionHand getItemUsageHand(@Nullable PlatformPlayer player) {
        if (nativePlayer(player) instanceof Player forgePlayer && forgePlayer.isUsingItem()) {
            return ForgeConversionUtil.fromForgeInteractionHand(forgePlayer.getUsedItemHand());
        }
        return null;
    }

    @Override
    public boolean isUsingItem(@Nullable PlatformPlayer player) {
        return nativePlayer(player) instanceof Player forgePlayer && forgePlayer.isUsingItem();
    }

    private static Object nativePlayer(@Nullable PlatformPlayer player) {
        return player == null ? null : player.getNative();
    }
}
