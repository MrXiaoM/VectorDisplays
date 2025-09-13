package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.hologram.vector.displays.hologram.utils.ItemsAdderHolder;
import top.mrxiaom.hologram.vector.displays.hologram.utils.ReplaceText;

public class HologramAPI {
    private static HologramManager hologram;
    private static ReplaceText replaceText;
    private static PlayerManager playerManager;

    public static HologramManager getHologram() {
        return hologram;
    }

    public static ReplaceText getReplaceText() {
        return replaceText;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    private JavaPlugin plugin;

    public void onLoad(JavaPlugin plugin) {
        this.plugin = plugin;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings()
                .customResourceProvider(plugin::getResource)
                .reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        PacketEvents.getAPI().init();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();

        EntityLib.init(platform, settings);

        playerManager = PacketEvents.getAPI().getPlayerManager();

        hologram = new HologramManager(plugin);

        try {
            replaceText = new ItemsAdderHolder();
        } catch (ClassNotFoundException exception) {
            replaceText = s -> s;
        }
    }

    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
