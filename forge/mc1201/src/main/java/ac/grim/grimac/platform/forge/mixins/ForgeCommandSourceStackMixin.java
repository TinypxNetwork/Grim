package ac.grim.grimac.platform.forge.mixins;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.sender.ForgeSenderFactory;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(CommandSourceStack.class)
@Implements(@Interface(iface = Sender.class, prefix = "grim$"))
public abstract class ForgeCommandSourceStackMixin {

    @Unique
    private CommandSourceStack grim$self() {
        return (CommandSourceStack) (Object) this;
    }

    @Unique
    private ForgeSenderFactory grim$factory() {
        return (ForgeSenderFactory) AbstractGrimACForgeLoaderPlugin.LOADER.getForgeSenderFactory();
    }

    public UUID grim$getUniqueId() {
        return grim$factory().getUniqueId(grim$self());
    }

    public String grim$getName() {
        return grim$factory().getName(grim$self());
    }

    public void grim$sendMessage(String message) {
        grim$factory().sendNativeMessage(grim$self(), message);
    }

    public void grim$sendMessage(Component message) {
        grim$factory().sendNativeMessage(grim$self(), message);
    }

    public boolean grim$hasPermission(String permission) {
        return grim$factory().hasPermission(grim$self(), permission);
    }

    public boolean grim$hasPermission(String permission, boolean defaultIfUnset) {
        return grim$factory().hasPermission(grim$self(), permission, defaultIfUnset);
    }

    public void grim$performCommand(String commandLine) {
        grim$factory().performNativeCommand(grim$self(), commandLine);
    }

    public boolean grim$isConsole() {
        return grim$factory().isConsole(grim$self());
    }

    public Object grim$getNativeSender() {
        return grim$self();
    }

    public @Nullable PlatformPlayer grim$getPlatformPlayer() {
        return GrimAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(grim$getUniqueId());
    }
}
