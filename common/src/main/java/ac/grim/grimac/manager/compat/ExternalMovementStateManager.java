package ac.grim.grimac.manager.compat;

import ac.grim.grimac.player.GrimPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ExternalMovementStateManager {
    private volatile ExternalMovementStateProvider provider = ExternalMovementStateProvider.EMPTY;

    public @NotNull ExternalMovementState getState(@NotNull GrimPlayer player) {
        return provider.getState(player);
    }

    public void setProvider(@NotNull ExternalMovementStateProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    public void clearProvider() {
        this.provider = ExternalMovementStateProvider.EMPTY;
    }
}
