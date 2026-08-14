package top.mrxiaom.hologram.vector.displays.api;

import org.bukkit.plugin.java.JavaPlugin;

public class Schedulers {
    public static IScheduler create(JavaPlugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return new FoliaScheduler(plugin);
        } catch (Throwable ignored) {
        }
        return new BukkitScheduler(plugin);
    }
}
