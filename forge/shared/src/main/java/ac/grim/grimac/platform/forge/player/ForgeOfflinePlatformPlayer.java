package ac.grim.grimac.platform.forge.player;

import ac.grim.grimac.platform.api.player.OfflinePlatformPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ForgeOfflinePlatformPlayer implements OfflinePlatformPlayer {
    private final UUID uuid;
    private final String name;

    public ForgeOfflinePlatformPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean isOnline() {
        return false;
    }
}
