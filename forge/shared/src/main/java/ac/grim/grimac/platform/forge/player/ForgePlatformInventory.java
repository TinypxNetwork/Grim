package ac.grim.grimac.platform.forge.player;

import ac.grim.grimac.platform.api.player.PlatformInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ForgePlatformInventory implements PlatformInventory {
    protected final ServerPlayer player;

    public ForgePlatformInventory(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public Object getItemInHand() {
        return player.getMainHandItem();
    }

    @Override
    public Object getItemInOffHand() {
        return player.getOffhandItem();
    }

    @Override
    public Object getItem(int slot) {
        return player.getInventory().getItem(slot);
    }

    @Override
    public int getSize() {
        return player.getInventory().getContainerSize();
    }

    @Override
    public boolean isHoldingItem() {
        return !player.getMainHandItem().isEmpty();
    }
}