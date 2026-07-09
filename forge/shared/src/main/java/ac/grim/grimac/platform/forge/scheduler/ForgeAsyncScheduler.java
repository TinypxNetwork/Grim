package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ForgeAsyncScheduler implements AsyncScheduler {
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);
    private static final ThreadFactory DAEMON_FACTORY = r -> {
        Thread t = new Thread(r, "grimac-async-" + THREAD_COUNTER.getAndIncrement());
        t.setDaemon(true);
        return t;
    };

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4, DAEMON_FACTORY);
    private final ConcurrentMap<GrimPlugin, List<Future<?>>> pluginTasks = new ConcurrentHashMap<>();

    @Override
    public TaskHandle runNow(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(task, executor);
        trackFuture(plugin, future);
        return ForgePlatformScheduler.createTaskHandle(false, future::isCancelled, () -> future.cancel(false));
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
        ScheduledFuture<?> future = executor.schedule(task, delay, timeUnit);
        trackFuture(plugin, future);
        return ForgePlatformScheduler.createTaskHandle(false, future::isCancelled, () -> future.cancel(false));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, delay, period, timeUnit);
        trackFuture(plugin, future);
        return ForgePlatformScheduler.createTaskHandle(false, future::isCancelled, () -> future.cancel(false));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return runAtFixedRate(plugin, task, initialDelayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancel(@NotNull GrimPlugin plugin) {
        List<Future<?>> futures = pluginTasks.remove(plugin);
        if (futures != null) {
            for (Future<?> future : futures) {
                future.cancel(false);
            }
        }
    }

    public void cancelAll() {
        for (GrimPlugin plugin : pluginTasks.keySet()) {
            cancel(plugin);
        }
        executor.shutdownNow();
    }

    private void trackFuture(GrimPlugin plugin, Future<?> future) {
        pluginTasks.computeIfAbsent(plugin, k -> new ArrayList<>()).add(future);
    }
}