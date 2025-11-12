package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {
    private final PluginWrapper plugin;
    private final List<AbstractEntity<?>> virtualEntities = new CopyOnWriteArrayList<>();
    private IRunTask updateTask;
    private long updatePeriod;
    public HologramManager(PluginWrapper plugin) {
        this.plugin = plugin;
        this.restartUpdateTimer(3 * 20L);
    }

    public void restartUpdateTimer(long period) {
        restartUpdateTimer(20L, period);
    }

    public void restartUpdateTimer(long delay, long period) {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }
        this.updateTask = plugin.getScheduler().runTaskTimerAsync(() -> {
            for (AbstractEntity<?> entity : virtualEntities) {
                entity.updateAffectedPlayers();
            }
        }, delay, this.updatePeriod = period);
    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    public PluginWrapper getPlugin() {
        return plugin;
    }

    public List<AbstractEntity<?>> getVirtualEntities() {
        return Collections.unmodifiableList(this.virtualEntities);
    }

    @Deprecated
    public List<AbstractEntity<?>> getHolograms() {
        return getVirtualEntities();
    }

    public void spawn(AbstractEntity<?> entity, Location location) {
        entity.spawn(location);
        if (!this.virtualEntities.contains(entity)) {
            this.virtualEntities.add(entity);
        }
    }

    public void register(AbstractEntity<?> entity) {
        if (!this.virtualEntities.contains(entity)) {
            this.virtualEntities.add(entity);
        }
    }

    public void remove(AbstractEntity<?> entity) {
        this.virtualEntities.remove(entity);
        entity.kill();
    }

    public void removeAll() {
        this.virtualEntities.forEach(AbstractEntity::kill);
        this.virtualEntities.clear();
    }

    protected void onDisable() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }
        removeAll();
    }
}
