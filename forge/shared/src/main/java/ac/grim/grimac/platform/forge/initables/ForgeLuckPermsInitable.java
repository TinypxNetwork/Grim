package ac.grim.grimac.platform.forge.initables;

import ac.grim.grimac.manager.init.OptionalReflectiveInitable;
import net.minecraftforge.fml.ModList;

public final class ForgeLuckPermsInitable extends OptionalReflectiveInitable {
    private static final String LUCKPERMS_MOD_ID = "luckperms";
    private static final String HANDLER_CLASS =
            "ac.grim.grimac.platform.forge.initables.ForgeLuckPermsHandler";

    public ForgeLuckPermsInitable() {
        super(HANDLER_CLASS, "Error when initializing LuckPerms hook");
    }

    @Override
    protected boolean isAvailable() {
        return ModList.get().isLoaded(LUCKPERMS_MOD_ID);
    }
}