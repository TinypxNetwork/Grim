package ac.grim.grimac.platform.forge;

import ac.grim.grimac.platform.api.PlatformPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fml.ModContainer;

import java.util.Objects;

public class ForgePlatformPlugin implements PlatformPlugin {
    private final @NotNull ModContainer modContainer;

    @Contract(pure = true)
    public ForgePlatformPlugin(@NotNull ModContainer modContainer) {
        this.modContainer = Objects.requireNonNull(modContainer);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return modContainer.getModId();
    }

    @Override
    public String getVersion() {
        return modContainer.getModInfo().getVersion().toString();
    }
}