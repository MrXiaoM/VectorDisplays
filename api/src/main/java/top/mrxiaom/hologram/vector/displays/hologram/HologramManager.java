package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {
    private final JavaPlugin plugin;
    private final List<TextHologram> holograms = new CopyOnWriteArrayList<>();
    public HologramManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<TextHologram> getHolograms() {
        return Collections.unmodifiableList(this.holograms);
    }

    public void spawn(TextHologram textHologram, Location location) {
        textHologram.spawn(location);
        if (!this.holograms.contains(textHologram)) {
            this.holograms.add(textHologram);
        }
    }

    public void register(TextHologram textHologram) {
        if (!this.holograms.contains(textHologram)) {
            this.holograms.add(textHologram);
        }
    }

    public void remove(TextHologram textHologram) {
        this.holograms.remove(textHologram);
        textHologram.kill();
    }

    public void removeAll() {
        this.holograms.forEach(TextHologram::kill);
        this.holograms.clear();
    }
}
