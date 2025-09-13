package top.mrxiaom.hologram.vector.displays.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BukkitScheduler implements IScheduler {
    public static class Task implements IRunTask {
        BukkitTask impl;
        public Task(BukkitTask impl) {
            this.impl = impl;
        }
        @Override
        public void cancel() {
            impl.cancel();
        }
    }
    JavaPlugin plugin;
    public BukkitScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public Task wrap(BukkitTask task) {
        return new Task(task);
    }

    @Override
    public @NotNull IRunTask runTask(@NotNull Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public @NotNull IRunTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    @Override
    public @NotNull IRunTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public @NotNull IRunTask runTaskAsync(@NotNull Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public @NotNull IRunTask runTaskLaterAsync(@NotNull Runnable runnable, long delay) {
        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay));
    }

    @Override
    public @NotNull IRunTask runTaskTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        return wrap(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period));
    }

    @Override
    public <T extends Entity> void runAtEntity(@NotNull T entity, @NotNull Consumer<T> runnable) {
        runnable.accept(entity);
    }

    @Override
    public <T extends Entity> @NotNull IRunTask runAtEntityLater(@NotNull T entity, @NotNull Consumer<T> runnable, long delay) {
        return runTaskLater(() -> runnable.accept(entity), delay);
    }

    @Override
    public <T extends Entity> @NotNull IRunTask runAtEntityTimer(@NotNull T entity, @NotNull Consumer<T> runnable, long delay, long period) {
        return runTaskTimer(() -> runnable.accept(entity), delay, period);
    }

    @Override
    public void runAtLocation(@NotNull Location location, @NotNull Consumer<Location> runnable) {
        runnable.accept(location);
    }

    @Override
    public @NotNull IRunTask runAtLocationLater(@NotNull Location location, @NotNull Consumer<Location> runnable, long delay) {
        return runTaskLater(() -> runnable.accept(location), delay);
    }

    @Override
    public @NotNull IRunTask runAtLocationTimer(@NotNull Location location, @NotNull Consumer<Location> runnable, long delay, long period) {
        return runTaskTimer(() -> runnable.accept(location), delay, period);
    }

    @Override
    public void teleport(@NotNull Entity entity, @NotNull Location location, PlayerTeleportEvent.@NotNull TeleportCause cause, Consumer<Entity> then) {
        entity.teleport(location, cause);
        if (then != null) then.accept(entity);
    }

    @Override
    public void teleport(@NotNull Entity entity, @NotNull Location location, Consumer<Entity> then) {
        entity.teleport(location);
        if (then != null) then.accept(entity);
    }

    @Override
    public void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
