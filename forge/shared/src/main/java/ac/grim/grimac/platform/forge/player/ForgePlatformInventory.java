package ac.grim.grimac.platform.forge.player;

import ac.grim.grimac.platform.api.player.PlatformInventory;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public class ForgePlatformInventory implements PlatformInventory {
    protected final ServerPlayer player;

    public ForgePlatformInventory(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public ItemStack getItemInHand() {
        return convert(player.getMainHandItem());
    }

    @Override
    public ItemStack getItemInOffHand() {
        return convert(player.getOffhandItem());
    }

    @Override
    public ItemStack getStack(int bukkitSlot, int vanillaSlot) {
        return convert(player.getInventory().getItem(vanillaSlot));
    }

    @Override
    public ItemStack getHelmet() {
        return convert(player.getInventory().getArmor(3));
    }

    @Override
    public ItemStack getChestplate() {
        return convert(player.getInventory().getArmor(2));
    }

    @Override
    public ItemStack getLeggings() {
        return convert(player.getInventory().getArmor(1));
    }

    @Override
    public ItemStack getBoots() {
        return convert(player.getInventory().getArmor(0));
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[player.getInventory().getContainerSize()];
        for (int slot = 0; slot < contents.length; slot++) {
            contents[slot] = convert(player.getInventory().getItem(slot));
        }
        return contents;
    }

    @Override
    public String getOpenInventoryKey() {
        return player.containerMenu == player.inventoryMenu ? "minecraft:inventory" : player.containerMenu.getClass().getName();
    }

    protected ItemStack convert(net.minecraft.world.item.ItemStack stack) {
        return AbstractGrimACForgeLoaderPlugin.LOADER.getForgeConversionUtil().fromForgeItemStack(stack);
    }
}
