package ac.grim.grimac.platform.forge.manager;

import ac.grim.grimac.platform.api.PlatformPlugin;
import ac.grim.grimac.platform.api.manager.PlatformPluginManager;
import ac.grim.grimac.platform.forge.ForgePlatformPlugin;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgePlatformPluginManager implements PlatformPluginManager {

    @Override
    public List<PlatformPlugin> getPluginList() {
        List<PlatformPlugin> plugins = new ArrayList<>();
        for (ModContainer mod : ModList.get().getMods()) {
            plugins.add(new ForgePlatformPlugin(mod));
        }
        return plugins;
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return ModList.get().isLoaded(pluginName);
    }
}