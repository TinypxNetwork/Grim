package ac.grim.grimac.platform.forge.scheduler;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.scheduler.RegionScheduler;
import ac.grim.grimac.platform.api.scheduler.TaskHandle;
import ac.grim.grimac.platform.api.world.PlatformWorld;
import ac.grim.grimac.platform.forge.AbstractGrimACForgeLoaderPlugin;
import ac.grim.grimac.platform.forge.ForgeServerEvents;
import ac.grim.grimac.utils.math.Location;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class ForgeRegionScheduler implements RegionScheduler {
    private final ConcurrentHashMap<ForgePlatformScheduler.ScheduledTask, Runnable> tasks = new ConcurrentHashMap<>();

    public ForgeRegionScheduler() {
        ForgeServerEvents.onEndTick(server -> ForgePlatformScheduler.handleSyncTasks(tasks, server));
    }

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
        run(plugin, world, chunkX, chunkZ, task);
    }

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
        run(plugin, location, task);
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
        return runDelayed(plugin, world, chunkX, chunkZ, task, 0);
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
        return runDelayed(plugin, location, task, 0);
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long delay) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + delay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task, tick, 0, false, plugin
        );
        tasks.put(scheduledTask, () -> tasks.remove(scheduledTask));
        return ForgePlatformScheduler.createTaskHandle(true, () -> !tasks.containsKey(scheduledTask), () -> tasks.remove(scheduledTask));
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long delay) {
        return runDelayed(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delay);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long initialDelay, long period) {
        long tick = AbstractGrimACForgeLoaderPlugin.FORGE_SERVER.getTickCount() + initialDelay;
        ForgePlatformScheduler.ScheduledTask scheduledTask = new ForgePlatformScheduler.ScheduledTask(
                task, tick, period, true, plugin
        );
        tasks.put(scheduledTask, () -> tasks.remove(scheduledTask));
        return ForgePlatformScheduler.createTaskHandle(true, () -> !tasks.containsKey(scheduledTask), () -> tasks.remove(scheduledTask));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long initialDelay, long period) {
        return runAtFixedRate(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, initialDelay, period);
    }

    public void cancelAll() {
        ForgePlatformScheduler.cancelAllTasks(tasks);
    }
}
