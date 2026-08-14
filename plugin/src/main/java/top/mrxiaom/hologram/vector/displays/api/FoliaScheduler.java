package top.mrxiaom.hologram.vector.displays.api;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaScheduler implements IScheduler {
    private final JavaPlugin plugin;
    private final AsyncScheduler asyncScheduler;
    private final RegionScheduler regionScheduler;
    private final GlobalRegionScheduler globalRegionScheduler;
    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.asyncScheduler = Bukkit.getAsyncScheduler();
        this.regionScheduler = Bukkit.getRegionScheduler();
        this.globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
    }

    public Task wrap(ScheduledTask task) {
        return new Task(task);
    }

    @Override
    public @NotNull IRunTask runTask(@NotNull Runnable runnable) {
        return wrap(globalRegionScheduler.run(plugin, (t) -> runnable.run()));
    }

    @Override
    public @NotNull IRunTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return wrap(globalRegionScheduler.runDelayed(plugin, (t) -> runnable.run(), delay));
    }

    @Override
    public @NotNull IRunTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        return wrap(globalRegionScheduler.runAtFixedRate(plugin, (t) -> runnable.run(), delay, period));
    }

    @Override
    public @NotNull IRunTask runTaskAsync(@NotNull Runnable runnable) {
        return wrap(asyncScheduler.runNow(plugin, (t) -> runnable.run()));
    }

    @Override
    public @NotNull IRunTask runTaskLaterAsync(@NotNull Runnable runnable, long delay) {
        return wrap(asyncScheduler.runDelayed(plugin, (t) -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public @NotNull IRunTask runTaskTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        return wrap(asyncScheduler.runAtFixedRate(plugin, (t) -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
    }

    @Override
    public <T extends Entity> void runAtEntity(@NotNull T entity, @NotNull Consumer<T> runnable) {
        entity.getScheduler().run(plugin, (t) -> runnable.accept(entity), null);
    }

    @Override
    public <T extends Entity> @NotNull IRunTask runAtEntityLater(@NotNull T entity, @NotNull Consumer<T> runnable, long delay) {
        return wrap(entity.getScheduler().runDelayed(plugin, (t) -> runnable.accept(entity), null, delay));
    }

    @Override
    public <T extends Entity> @NotNull IRunTask runAtEntityTimer(@NotNull T entity, @NotNull Consumer<T> runnable, long delay, long period) {
        return wrap(entity.getScheduler().runAtFixedRate(plugin, (t) -> runnable.accept(entity), null, delay, period));
    }

    @Override
    public void runAtLocation(@NotNull Location location, @NotNull Consumer<Location> runnable) {
        regionScheduler.run(plugin, location, (t) -> runnable.accept(location));
    }

    @Override
    public @NotNull IRunTask runAtLocationLater(@NotNull Location location, @NotNull Consumer<Location> runnable, long delay) {
        return wrap(regionScheduler.runDelayed(plugin, location, (t) -> runnable.accept(location), delay));
    }

    @Override
    public @NotNull IRunTask runAtLocationTimer(@NotNull Location location, @NotNull Consumer<Location> runnable, long delay, long period) {
        return wrap(regionScheduler.runAtFixedRate(plugin, location, (t) -> runnable.accept(location), delay, period));
    }

    @Override
    public void teleport(@NotNull Entity entity, @NotNull Location location, PlayerTeleportEvent.@NotNull TeleportCause cause, @Nullable Consumer<Entity> then) {
        CompletableFuture<Boolean> future = entity.teleportAsync(location, cause);
        if (then != null) future.thenRun(() -> then.accept(entity));
    }

    @Override
    public void teleport(@NotNull Entity entity, @NotNull Location location, @Nullable Consumer<Entity> then) {
        CompletableFuture<Boolean> future = entity.teleportAsync(location);
        if (then != null) future.thenRun(() -> then.accept(entity));
    }

    @Override
    public void cancelTasks() {
        asyncScheduler.cancelTasks(plugin);
        globalRegionScheduler.cancelTasks(plugin);
    }

    public static class Task implements IRunTask {
        private final ScheduledTask task;
        public Task(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }
}
