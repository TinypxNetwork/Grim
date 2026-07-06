package ac.grim.grimac.platform.forge;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ForgeServerEvents {

    private ForgeServerEvents() {
    }

    private static final List<Consumer<MinecraftServer>> STARTING = new ArrayList<>();
    private static final List<Consumer<MinecraftServer>> STOPPING = new ArrayList<>();
    private static final List<Consumer<MinecraftServer>> END_TICK = new ArrayList<>();

    public static void onServerStarting(Consumer<MinecraftServer> listener) {
        STARTING.add(listener);
    }

    public static void onServerStopping(Consumer<MinecraftServer> listener) {
        STOPPING.add(listener);
    }

    public static void onEndTick(Consumer<MinecraftServer> listener) {
        END_TICK.add(listener);
    }

    public static void fireServerStarting(MinecraftServer server) {
        for (Consumer<MinecraftServer> listener : STARTING) listener.accept(server);
    }

    public static void fireServerStopping(MinecraftServer server) {
        for (Consumer<MinecraftServer> listener : STOPPING) listener.accept(server);
    }

    public static void fireEndTick(MinecraftServer server) {
        for (Consumer<MinecraftServer> listener : END_TICK) listener.accept(server);
    }
}