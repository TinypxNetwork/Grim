package ac.grim.grimac.platform.forge.mc1201.player;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeConversionUtil;
import ac.grim.grimac.platform.forge.player.AbstractForgePlatformPlayer;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class Forge1201PlatformPlayer extends AbstractForgePlatformPlayer {

    public Forge1201PlatformPlayer(ServerPlayer player) {
        super(player);
    }

    @Override
    public Sender getSender() {
        return AbstractGrimACForgeLoaderPlugin.LOADER.getForgeSenderFactory().wrap(
                serverPlayer().createCommandSourceStack()
        );
    }

    @Override
    public void kickPlayer(String textReason) {
        serverPlayer().connection.disconnect(
                (net.minecraft.network.chat.Component) AbstractGrimACForgeLoaderPlugin.LOADER.getForgeMessageUtils().textLiteral(textReason)
        );
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        serverPlayer().setGameMode(ForgeConversionUtil.toForgeGameMode(gameMode));
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(ac.grim.grimac.utils.math.Location location) {
        return CompletableFuture.supplyAsync(() -> {
            serverPlayer().teleportTo(
                    (net.minecraft.server.level.ServerLevel) serverPlayer().getLevel(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
            return true;
        });
    }
}