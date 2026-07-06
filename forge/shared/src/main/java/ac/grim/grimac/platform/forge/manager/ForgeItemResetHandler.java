package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.manager.ItemResetHandler;
import ac.grim.grimac.platform.forge.ForgeConversionUtil;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class ForgeItemResetHandler implements ItemResetHandler {

    @Override
    public void stopUsingItem(Object player) {
        if (player instanceof Player forgePlayer) {
            forgePlayer.stopUsingItem();
        }
    }

    @Override
    public InteractionHand getUsedItemHand(Object player) {
        if (player instanceof Player forgePlayer) {
            return ForgeConversionUtil.fromForgeInteractionHand(forgePlayer.getUsedItemHand());
        }
        return InteractionHand.MAIN_HAND;
    }
}