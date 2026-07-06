package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.scheduler.GlobalRegionScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeServerEvents;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class ForgeGlobalRegionScheduler implements GlobalRegionScheduler {
    private final ConcurrentHashMap<ForgePlatformScheduler.ScheduledTask, Runnable> tasks = new ConcurrentHashMap<>();

    public ForgeGlobalRegionScheduler() {
        ForgeServerEvents.onEndTick(server -> ForgePlatformScheduler.handleSyncTasks(tasks, server));
    }

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
        run(plugin, task);
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount();
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task, tick, 0, false, plugin
        );
        tasks.put(scheduledTask, () -> tasks.remove(scheduledTask));
        return ForgePlatformScheduler.createTaskHandle(true, () -> !tasks.containsKey(scheduledTask), () -> tasks.remove(scheduledTask));
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + delay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task, tick, 0, false, plugin
        );
        tasks.put(scheduledTask, () -> tasks.remove(scheduledTask));
        return ForgePlatformScheduler.createTaskHandle(true, () -> !tasks.containsKey(scheduledTask), () -> tasks.remove(scheduledTask));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelay, long period) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + initialDelay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task, tick, period, true, plugin
        );
        tasks.put(scheduledTask, () -> tasks.remove(scheduledTask));
        return ForgePlatformScheduler.createTaskHandle(true, () -> !tasks.containsKey(scheduledTask), () -> tasks.remove(scheduledTask));
    }

    @Override
    public void cancel(@NotNull GrimPlugin plugin) {
        ForgePlatformScheduler.cancelPluginTasks(tasks, plugin);
    }

    public void cancelAll() {
        ForgePlatformScheduler.cancelAllTasks(tasks);
    }
}
