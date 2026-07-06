package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ForgeEntityScheduler implements EntityScheduler {

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull GrimEntity entity, @NotNull Consumer<Object> task, @NotNull Consumer<Object> retired) {
        CompletableFuture.runAsync(() -> {
            if (entity.isAlive()) {
                task.accept(null);
            } else {
                retired.accept(null);
            }
        });
        return () -> {};
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull GrimEntity entity, @NotNull Consumer<Object> task, @NotNull Consumer<Object> retired, long delay) {
        CompletableFuture.runAsync(() -> {
            if (entity.isAlive()) {
                task.accept(null);
            } else {
                retired.accept(null);
            }
        });
        return () -> {};
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull GrimEntity entity, @NotNull Consumer<Object> task, @NotNull Consumer<Object> retired, long initialDelay, long period) {
        CompletableFuture.runAsync(() -> {
            if (entity.isAlive()) {
                task.accept(null);
            } else {
                retired.accept(null);
            }
        });
        return () -> {};
    }

    public void cancelAll() {
    }
}