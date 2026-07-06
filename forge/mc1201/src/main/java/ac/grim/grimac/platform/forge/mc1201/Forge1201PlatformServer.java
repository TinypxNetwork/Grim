package ac.grim.grimac.platform.forge.mc1201;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.forge.AbstractForgePlatformServer;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class Forge1201PlatformServer extends AbstractForgePlatformServer {

    @Override
    public int getOperatorPermissionLevel() {
        return AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getOperatorUserPermissionLevel();
    }

    @Override
    public boolean hasPermission(Sender sender, int level) {
        return ((CommandSourceStack) (Object) sender).hasPermission(level);
    }

    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSourceStack commandSource = (CommandSourceStack) (Object) sender;
        AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getCommands().performPrefixedCommand(commandSource, command);
    }

    @Override
    public double getTPS() {
        MinecraftServer server = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER;
        return Math.min(1000.0 / server.getAverageTickTime(), 20.0);
    }

    @Override
    public String getMinecraftVersion() {
        return AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getServerVersion();
    }

    @Override
    public double getAverageTickTime() {
        return AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getAverageTickTime();
    }
}