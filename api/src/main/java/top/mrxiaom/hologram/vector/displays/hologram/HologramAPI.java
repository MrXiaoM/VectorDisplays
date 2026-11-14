package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
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
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;

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
    private static SpectatorManager spectator;
    private static DisplayPassengerManager passengers;
    private static ReplaceText replaceText;
    private static PlayerManager playerManager;
    private static final Set<UUID> loadedPlayersMap = new HashSet<>();

    public static HologramManager getHologram() {
        return hologram;
    }

    @ApiStatus.Internal
    public static DisplayPassengerManager getPassengers() {
        return passengers;
    }

    public static SpectatorManager getSpectator() {
        return spectator;
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

        if (ServerVersion.getLatest().getProtocolVersion() < 776) {
            throw new UnsupportedOperationException("packetevents 版本过低，请更新到 2.13.0 或以上");
        }

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin.getPlugin());
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();

        EntityLib.init(platform, settings);

        plugin.getPlugin().getLogger().info("已加载服务端版本支持 " + NMS.getFactory().getName());

        playerManager = PacketEvents.getAPI().getPlayerManager();
        hologram = new HologramManager(plugin);
        spectator = new SpectatorManager(plugin, hologram);
        passengers = new DisplayPassengerManager();
        PacketEvents.getAPI().getEventManager().registerListener(passengers);

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
        if (hologram != null) {
            hologram.onDisable();
        }
        if (spectator != null) {
            spectator.onDisable();
        }
        if (passengers != null) {
            passengers.close();
            PacketEvents.getAPI().getEventManager().unregisterListener(passengers);
        }
        if (!PacketEvents.class.getName().startsWith(apiPackage)) {
            PacketEvents.getAPI().terminate();
        }
        HandlerList.unregisterAll(this);
        loadedPlayersMap.clear();
    }
}
