package ac.grim.grimac.platform.forge.sender;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractForgePlatformServer;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeMessageUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.rcon.RconConsoleSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ForgeSenderFactory extends AbstractForgeSenderFactory<CommandSourceStack> {

    private final AbstractForgePlatformServer platformServer = (AbstractForgePlatformServer) AbstractGrimACForgeLoaderPlugin.LOADER.getPlatformServer();
    private final ForgeMessageUtil forgeMessageUtils = AbstractGrimACForgeLoaderPlugin.LOADER.getForgeMessageUtils();

    @Override
    public @NotNull Sender wrap(@NotNull CommandSourceStack sender) {
        return Objects.requireNonNull(sender, "sender");
    }

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
        forgeMessageUtils.sendMessage((Sender) (Object) sender, forgeMessageUtils.textLiteral(message), false);
    }

    @Override
    protected void sendMessage(CommandSourceStack sender, Component message) {
        net.minecraft.network.chat.Component nativeText =
                (net.minecraft.network.chat.Component) AbstractGrimACForgeLoaderPlugin.LOADER.getForgeConversionUtil().toNativeText(message);
        forgeMessageUtils.sendMessage((Sender) (Object) sender, nativeText, false);
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
        return platformServer.hasPermission((Sender) (Object) sender, platformServer.getOperatorPermissionLevel());
    }

    @Override
    protected void performCommand(CommandSourceStack sender, String command) {
        platformServer.dispatchCommand((Sender) (Object) sender, command);
    }

    @Override
    public boolean isConsole(CommandSourceStack sender) {
        return sender.source == sender.getServer()
                || sender.source.getClass() == RconConsoleSource.class
                || (sender.source == CommandSourceStack.NULL && sender.getTextName().isEmpty());
    }

    @Override
    public boolean isPlayer(CommandSourceStack sender) {
        return sender.getEntity() instanceof ServerPlayer;
    }

    @SuppressWarnings("unchecked")
    public CommandSourceStack unwrap(Sender sender) {
        return (CommandSourceStack) (Object) sender;
    }
}