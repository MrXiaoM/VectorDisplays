package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {
    private final PluginWrapper plugin;
    private final List<AbstractEntity<?>> virtualEntities = new CopyOnWriteArrayList<>();
    public HologramManager(PluginWrapper plugin) {
        this.plugin = plugin;
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

    public void spawn(AbstractEntity<?> textHologram, Location location) {
        textHologram.spawn(location);
        if (!this.virtualEntities.contains(textHologram)) {
            this.virtualEntities.add(textHologram);
        }
    }

    public void register(AbstractEntity<?> textHologram) {
        if (!this.virtualEntities.contains(textHologram)) {
            this.virtualEntities.add(textHologram);
        }
    }

    public void remove(AbstractEntity<?> textHologram) {
        this.virtualEntities.remove(textHologram);
        textHologram.kill();
    }

    public void removeAll() {
        this.virtualEntities.forEach(AbstractEntity::kill);
        this.virtualEntities.clear();
    }
}
