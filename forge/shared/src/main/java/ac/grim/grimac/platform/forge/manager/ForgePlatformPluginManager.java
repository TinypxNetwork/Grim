package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.PlatformPlugin;
import ac.grim.grimac.platform.api.manager.PlatformPluginManager;
import ac.grim.grimac.platform.forge.ForgePlatformPlugin;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.ModList;

public class ForgePlatformPluginManager implements PlatformPluginManager {

    @Override
    public PlatformPlugin[] getPlugins() {
        PlatformPlugin[] plugins = new PlatformPlugin[ModList.get().getMods().size()];
        int index = 0;
        for (IModInfo mod : ModList.get().getMods()) {
            plugins[index++] = new ForgePlatformPlugin(mod);
        }
        return plugins;
    }

    @Override
    public PlatformPlugin getPlugin(String pluginName) {
        return ModList.get().getMods().stream()
                .filter(mod -> mod.getModId().equals(pluginName))
                .findFirst()
                .map(ForgePlatformPlugin::new)
                .orElse(null);
    }
}
