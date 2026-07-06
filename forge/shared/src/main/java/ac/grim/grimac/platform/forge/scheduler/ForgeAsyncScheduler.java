package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ForgeAsyncScheduler implements AsyncScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final ConcurrentHashMap<ScheduledFuture<?>, Runnable> cancellationCallbacks = new ConcurrentHashMap<>();

    @Override
    public TaskHandle runNow(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> task.accept(null), executor);
        return () -> future.cancel(false);
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task, long delay) {
        ScheduledFuture<?> future = executor.schedule(() -> task.accept(null), delay, TimeUnit.MILLISECONDS);
        return () -> future.cancel(false);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task, long initialDelay, long period) {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> task.accept(null), initialDelay, period, TimeUnit.MILLISECONDS);
        return () -> future.cancel(false);
    }

    public void cancelAll() {
        executor.shutdownNow();
        ForgePlatformScheduler.cancelAllTasks(cancellationCallbacks);
    }
}