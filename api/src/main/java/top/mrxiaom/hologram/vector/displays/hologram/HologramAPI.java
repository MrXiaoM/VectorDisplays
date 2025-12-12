package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;
import top.mrxiaom.hologram.vector.displays.hologram.utils.ItemsAdderHolder;
import top.mrxiaom.hologram.vector.displays.hologram.utils.ReplaceText;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HologramAPI implements Listener {
    // com.github.retrooper.packetevents
    private static final String apiPackage = String.valueOf(new char[] {
            'c','o','m',
            '/',
            'g','i','t','h','u','b',
            '/',
            'r','e','t','r','o','o','p','e','r',
            '/',
            'p','a','c','k','e','t','e','v','e','n','t','s'
    }).replace('/', '.');
    private static HologramManager hologram;
    private static ReplaceText replaceText;
    private static PlayerManager playerManager;
    private static final Set<UUID> loadedPlayersMap = new HashSet<>();

    public static HologramManager getHologram() {
        return hologram;
    }

    public static ReplaceText getReplaceText() {
        return replaceText;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    private final PluginWrapper plugin;
    public HologramAPI(PluginWrapper plugin) {
        this.plugin = plugin;
    }

    @ApiStatus.Internal
    protected static boolean shouldShowToNearby(Player player) {
        return loadedPlayersMap.contains(player.getUniqueId());
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public void onLoad() {
        if (!PacketEvents.class.getName().startsWith(apiPackage)) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin.getPlugin()));
            PacketEvents.getAPI().getSettings()
                    .customResourceProvider(plugin::getResource)
                    .reEncodeByDefault(false)
                    .checkForUpdates(false)
                    .bStats(false);
            PacketEvents.getAPI().load();
        }
    }

    public void onEnable() {
        if (!PacketEvents.class.getName().startsWith(apiPackage)) {
            PacketEvents.getAPI().init();
        }

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin.getPlugin());
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();

        EntityLib.init(platform, settings);

        playerManager = PacketEvents.getAPI().getPlayerManager();
        hologram = new HologramManager(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin.getPlugin());
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadedPlayersMap.add(player.getUniqueId());
        }

        try {
            replaceText = new ItemsAdderHolder();
        } catch (ClassNotFoundException exception) {
            replaceText = s -> s;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        loadedPlayersMap.remove(uuid);
        plugin.getScheduler().runTaskLater(() -> loadedPlayersMap.add(uuid), 2L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        loadedPlayersMap.remove(e.getPlayer().getUniqueId());
    }

    public void onDisable() {
        hologram.onDisable();
        if (!PacketEvents.class.getName().startsWith(apiPackage)) {
            PacketEvents.getAPI().terminate();
        }
        HandlerList.unregisterAll(this);
        loadedPlayersMap.clear();
    }
}
