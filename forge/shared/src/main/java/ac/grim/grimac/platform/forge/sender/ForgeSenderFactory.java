package ac.grim.grimac.platform.forge.sender;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractForgePlatformServer;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.rcon.RconConsoleSource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ForgeSenderFactory extends AbstractForgeSenderFactory<CommandSourceStack> {

    private final AbstractForgePlatformServer platformServer = (AbstractForgePlatformServer) AbstractGrimACForgeLoaderPlugin.LOADER.getPlatformServer();

    @Override
    public UUID getUniqueId(CommandSourceStack commandSource) {
        if (commandSource.getEntity() != null) {
            return commandSource.getEntity().getUUID();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(CommandSourceStack commandSource) {
        String name = commandSource.getTextName();
        if (commandSource.getEntity() != null && name.equals("Server")) {
            return Sender.CONSOLE_NAME;
        }
        return name;
    }

    @Override
    protected void sendMessage(CommandSourceStack sender, String message) {
        sender.sendSuccess(() -> net.minecraft.network.chat.Component.literal(message), false);
    }

    @Override
    protected void sendMessage(CommandSourceStack sender, Component message) {
        net.minecraft.network.chat.Component nativeText =
                (net.minecraft.network.chat.Component) AbstractGrimACForgeLoaderPlugin.LOADER.getForgeConversionUtil().toNativeText(message);
        sender.sendSuccess(() -> nativeText, false);
    }

    public void sendNativeMessage(CommandSourceStack sender, String message) {
        sendMessage(sender, message);
    }

    public void sendNativeMessage(CommandSourceStack sender, Component message) {
        sendMessage(sender, message);
    }

    @Override
    protected @Nullable Boolean queryPermissionValue(CommandSourceStack sender, String node) {
        return sender.hasPermission(2) ? true : null;
    }

    @Override
    protected boolean queryPermission(CommandSourceStack sender, String node, boolean defaultIfUnset) {
        return sender.hasPermission(2);
    }

    @Override
    protected boolean isOperator(CommandSourceStack sender) {
        return sender.hasPermission(platformServer.getOperatorPermissionLevel());
    }

    @Override
    protected void performCommand(CommandSourceStack sender, String command) {
        sender.getServer().getCommands().performPrefixedCommand(sender, command);
    }

    public void performNativeCommand(CommandSourceStack sender, String command) {
        performCommand(sender, command);
    }

    @Override
    public boolean isConsole(CommandSourceStack sender) {
        return sender.source == sender.getServer()
                || sender.source.getClass() == RconConsoleSource.class
                || (sender.getEntity() == null && sender.getTextName().isEmpty());
    }

    @Override
    public boolean isPlayer(CommandSourceStack sender) {
        return sender.getEntity() instanceof ServerPlayer;
    }
}
