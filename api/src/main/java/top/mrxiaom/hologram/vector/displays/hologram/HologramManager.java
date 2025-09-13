package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager {
    private final JavaPlugin plugin;
    private final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();
    public HologramManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<String, TextHologram> getHologramsMap() {
        return hologramsMap;
    }

    public List<TextHologram> getHolograms() {
        return new ArrayList<>(this.hologramsMap.values());
    }

    public void spawn(TextHologram textHologram, Location location) {
        textHologram.spawn(location);
        this.hologramsMap.put(textHologram.getId(), textHologram);
    }

    public void register(TextHologram textHologram) {
        this.hologramsMap.put(textHologram.getId(), textHologram);
    }

    public void remove(TextHologram textHologram) {
        remove(textHologram.getId());
    }

    public void remove(String id) {
        Optional.ofNullable(this.hologramsMap.remove(id)).ifPresent(TextHologram::kill);
    }

    public void removeAll() {
        this.hologramsMap.values().forEach(TextHologram::kill);
        this.hologramsMap.clear();
    }
}
