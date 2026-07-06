package ac.grim.grimac.platform.forge;

import ac.grim.grimac.platform.api.PlatformServer;
import ac.grim.grimac.platform.api.sender.Sender;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractForgePlatformServer implements PlatformServer {

    public abstract int getOperatorPermissionLevel();

    public abstract boolean hasPermission(Sender sender, int level);

    @Override
    public String getPlatformImplementationString() {
        return "Forge " + FMLLoader.getVersionInfo().mcAndForgeVersion() + " (MC: " + getMinecraftVersion() + ")";
    }

    @Override
    public Sender getConsoleSender() {
        return AbstractGrimACForgeLoaderPlugin.LOADER.getForgeSenderFactory().wrap(
                AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.createCommandSourceStack()
        );
    }

    @Override
    public void registerOutgoingPluginChannel(String name) {
    }

    @Nullable
    public abstract String getMinecraftVersion();

    public abstract double getAverageTickTime();
}