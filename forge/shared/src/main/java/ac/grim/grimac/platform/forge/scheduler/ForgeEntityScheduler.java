package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ForgeEntityScheduler implements EntityScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    @Override
    public void execute(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable run, Runnable retired, long delay) {
        runDelayed(entity, plugin, run, retired, delay);
    }

    @Override
    public TaskHandle run(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, Runnable retired) {
        return runDelayed(entity, plugin, task, retired, 0);
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, Runnable retired, long delayTicks) {
        ScheduledFuture<?> future = executor.schedule(
                () -> runOrRetire(entity, task, retired),
                delayTicks * 50L,
                TimeUnit.MILLISECONDS
        );
        return ForgePlatformScheduler.createTaskHandle(false, future::isCancelled, () -> future.cancel(false));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, Runnable retired, long initialDelayTicks, long periodTicks) {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                () -> runOrRetire(entity, task, retired),
                initialDelayTicks * 50L,
                periodTicks * 50L,
                TimeUnit.MILLISECONDS
        );
        return ForgePlatformScheduler.createTaskHandle(false, future::isCancelled, () -> future.cancel(false));
    }

    public void cancelAll() {
        executor.shutdownNow();
    }

    private static void runOrRetire(GrimEntity entity, Runnable task, Runnable retired) {
        if (!entity.isDead()) {
            task.run();
        } else if (retired != null) {
            retired.run();
        }
    }
}