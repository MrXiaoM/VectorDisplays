package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {
    private final PluginWrapper plugin;
    private final List<TextHologram> holograms = new CopyOnWriteArrayList<>();
    public HologramManager(PluginWrapper plugin) {
        this.plugin = plugin;
    }

    public PluginWrapper getPlugin() {
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
