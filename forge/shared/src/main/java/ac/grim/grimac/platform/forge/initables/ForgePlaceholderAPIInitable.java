package ac.grim.grimac.platform.forge.initables;

import ac.grim.grimac.manager.init.OptionalReflectiveInitable;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import net.minecraftforge.fml.ModList;

public final class ForgePlaceholderAPIInitable extends OptionalReflectiveInitable {
    private static final String PLACEHOLDERAPI_MOD_ID = "placeholderapi";
    private static final String HANDLER_CLASS =
            "ac.grim.grimac.platform.forge.initables.ForgePlaceholderAPIExpansion";

    public ForgePlaceholderAPIInitable() {
        super(HANDLER_CLASS, "Failed to register GrimAC PlaceholderAPI expansion");
    }

    @Override
    protected boolean isAvailable() {
        return ModList.get().isLoaded(PLACEHOLDERAPI_MOD_ID);
    }
}