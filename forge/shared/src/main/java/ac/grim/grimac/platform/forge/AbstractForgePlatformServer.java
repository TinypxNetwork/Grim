package ac.grim.grimac.platform.forge;

import ac.grim.grimac.platform.api.PlatformServer;
import ac.grim.grimac.platform.api.sender.Sender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractForgePlatformServer implements PlatformServer {

    public abstract int getOperatorPermissionLevel();

    public abstract boolean hasPermission(Sender sender, int level);

    @Override
    public String getPlatformImplementationString() {
        return "Forge " + ForgeVersion.getVersion() + " (MC: " + getMinecraftVersion() + ")";
    }

    @Override
    public Sender getConsoleSender() {
        CommandSourceStack source = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.createCommandSourceStack();
        return ((ac.grim.grimac.platform.api.sender.SenderFactory<CommandSourceStack>)
                AbstractGrimACForgeLoaderPlugin.LOADER.getForgeSenderFactory()).wrap(source);
    }

    @Override
    public void registerOutgoingPluginChannel(String name) {
    }

    @Nullable
    public abstract String getMinecraftVersion();

    public abstract double getAverageTickTime();
}
