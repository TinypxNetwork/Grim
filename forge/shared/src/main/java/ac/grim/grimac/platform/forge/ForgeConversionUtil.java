package ac.grim.grimac.platform.forge;

import ac.grim.grimac.utils.anticheat.LogUtil;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

public class ForgeConversionUtil {

    public static GameType toForgeGameMode(GameMode gameMode) {
        return switch (gameMode) {
            case CREATIVE -> GameType.CREATIVE;
            case SURVIVAL -> GameType.SURVIVAL;
            case ADVENTURE -> GameType.ADVENTURE;
            case SPECTATOR -> GameType.SPECTATOR;
        };
    }

    public static GameMode fromForgeGameMode(GameType forgeGameMode) {
        return switch (forgeGameMode) {
            case CREATIVE -> GameMode.CREATIVE;
            case SURVIVAL -> GameMode.SURVIVAL;
            case ADVENTURE -> GameMode.ADVENTURE;
            case SPECTATOR -> GameMode.SPECTATOR;
            default -> throw new IllegalArgumentException("Unknown Forge GameMode: " + forgeGameMode);
        };
    }

    @Nullable
    public static InteractionHand fromForgeInteractionHand(@Nullable net.minecraft.world.InteractionHand hand) {
        return hand == null ? null : switch (hand) {
            case OFF_HAND -> InteractionHand.OFF_HAND;
            case MAIN_HAND -> InteractionHand.MAIN_HAND;
        };
    }

    public ItemStack fromForgeItemStack(Object forgeItemStack) {
        net.minecraft.world.item.ItemStack forgeStack = (net.minecraft.world.item.ItemStack) forgeItemStack;
        if (forgeStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(buffer);
            packetByteBuf.writeItem(forgeStack);
            PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
            return wrapper.readItemStack();
        } catch (Exception e) {
            LogUtil.error("Failed to encode ItemStack: " + forgeStack, e);
            return ItemStack.EMPTY;
        } finally {
            ByteBufHelper.release(buffer);
        }
    }

    public Object toNativeText(Component component) {
        return net.minecraft.network.chat.Component.Serializer.fromJson(
                GsonComponentSerializer.gson().serializeToTree(component)
        );
    }

    public GameMode fromNativeGameMode(Object gameMode) {
        if (gameMode instanceof GameType) {
            return fromForgeGameMode((GameType) gameMode);
        }
        throw new IllegalArgumentException("Unknown game mode type: " + gameMode.getClass().getName());
    }

    public Object toNativeGameMode(GameMode gameMode) {
        return toForgeGameMode(gameMode);
    }
}