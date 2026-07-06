package ac.grim.grimac.platform.forge.player;

import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.player.BlockTranslator;
import ac.grim.grimac.platform.api.player.PlatformInventory;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeConversionUtil;
import ac.grim.grimac.platform.forge.entity.ForgeGrimEntity;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPluginMessage;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractForgePlatformPlayer extends ForgeGrimEntity implements PlatformPlayer {
    protected final ForgePlatformInventory inventory;

    public AbstractForgePlatformPlayer(ServerPlayer player) {
        super(player);
        this.inventory = new ForgePlatformInventory(player);
    }

    public ServerPlayer serverPlayer() {
        return (ServerPlayer) this.entity;
    }

    @Override
    public boolean isSneaking() {
        return serverPlayer().isShiftKeyDown();
    }

    @Override
    public void setSneaking(boolean isSneaking) {
        serverPlayer().setShiftKeyDown(isSneaking);
    }

    @Override
    public boolean hasPermission(String permission) {
        return getSender().hasPermission(permission);
    }

    @Override
    public boolean hasPermission(String permission, boolean defaultIfUnset) {
        return getSender().hasPermission(permission, defaultIfUnset);
    }

    @Override
    public void sendMessage(String message) {
        serverPlayer().sendSystemMessage(
                (net.minecraft.network.chat.Component) AbstractGrimACForgeLoaderPlugin.LOADER.getForgeMessageUtils().textLiteral(message),
                false
        );
    }

    @Override
    public void sendMessage(Component message) {
        Object nativeText = AbstractGrimACForgeLoaderPlugin.LOADER.getForgeConversionUtil().toNativeText(message);
        serverPlayer().sendSystemMessage((net.minecraft.network.chat.Component) nativeText, false);
    }

    @Override
    public boolean isOnline() {
        return !serverPlayer().hasDisconnected();
    }

    @Override
    public String getName() {
        return serverPlayer().getGameProfile().getName();
    }

    @Override
    public void updateInventory() {
        serverPlayer().containerMenu.broadcastChanges();
    }

    @Override
    public UUID getUniqueId() {
        return serverPlayer().getUUID();
    }

    @Override
    public Vector3d getPosition() {
        return new Vector3d(serverPlayer().getX(), serverPlayer().getY(), serverPlayer().getZ());
    }

    @Override
    public PlatformInventory getInventory() {
        return inventory;
    }

    @Override
    public GrimEntity getVehicle() {
        return serverPlayer().getVehicle() != null
                ? new ForgeGrimEntity(serverPlayer().getVehicle())
                : null;
    }

    @Override
    public com.github.retrooper.packetevents.protocol.player.GameMode getGameMode() {
        return ForgeConversionUtil.fromForgeGameMode(serverPlayer().gameMode.getGameModeForPlayer());
    }

    @Override
    public boolean isExternalPlayer() {
        return false;
    }

    @Override
    public BlockTranslator getBlockTranslator() {
        return BlockTranslator.IDENTITY;
    }

    @Override
    public void sendPluginMessage(String channelName, byte[] byteArray) {
        Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(serverPlayer().getUUID());
        User user = PacketEvents.getAPI().getProtocolManager().getUser(channel);
        if (user != null) {
            user.sendPacket(new WrapperPlayServerPluginMessage(channelName, byteArray));
        }
    }

    @Override
    public void replaceNativePlayer(Object nativePlayerObject) {
    }

    @Override
    public boolean isDead() {
        return serverPlayer().isDeadOrDying();
    }
}
