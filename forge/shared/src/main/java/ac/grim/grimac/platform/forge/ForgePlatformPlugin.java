package ac.grim.grimac.platform.forge;

import ac.grim.grimac.platform.api.PlatformPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Objects;

public class ForgePlatformPlugin implements PlatformPlugin {
    private final @NotNull IModInfo modInfo;

    @Contract(pure = true)
    public ForgePlatformPlugin(@NotNull IModInfo modInfo) {
        this.modInfo = Objects.requireNonNull(modInfo);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return modInfo.getModId();
    }

    @Override
    public String getVersion() {
        return modInfo.getVersion().toString();
    }
}
