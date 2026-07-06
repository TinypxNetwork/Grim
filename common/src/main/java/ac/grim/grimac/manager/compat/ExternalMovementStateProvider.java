package ac.grim.grimac.manager.compat;

import ac.grim.grimac.player.GrimPlayer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ExternalMovementStateProvider {
    ExternalMovementStateProvider EMPTY = player -> ExternalMovementState.EMPTY;

    @NotNull ExternalMovementState getState(@NotNull GrimPlayer player);
}
