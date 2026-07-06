package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.scheduler.RegionScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeServerEvents;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ForgeRegionScheduler implements RegionScheduler {
    private final ConcurrentHashMap<ForgePlatformScheduler.ScheduledTask, Runnable> tasks = new ConcurrentHashMap<>();

    public ForgeRegionScheduler() {
        ForgeServerEvents.onEndTick(server -> ForgePlatformScheduler.handleSyncTasks(tasks, server));
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task) {
        return runDelayed(plugin, task, 0);
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task, long delay) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + delay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task::accept, tick, 0, false, plugin
        );
        tasks.put(scheduledTask, null);
        return () -> tasks.remove(scheduledTask);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Consumer<Object> task, long initialDelay, long period) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + initialDelay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task::accept, tick, period, true, plugin
        );
        tasks.put(scheduledTask, null);
        return () -> tasks.remove(scheduledTask);
    }

    public void cancelAll() {
        ForgePlatformScheduler.cancelAllTasks(tasks);
    }
}