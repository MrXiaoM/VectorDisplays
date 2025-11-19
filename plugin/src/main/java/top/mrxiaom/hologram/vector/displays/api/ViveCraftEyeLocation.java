package top.mrxiaom.hologram.vector.displays.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;
import top.mrxiaom.hologram.vector.displays.TerminalManager;
import top.mrxiaom.hologram.vector.displays.VectorDisplays;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViveCraftEyeLocation implements IEyeLocationAdapter, Listener {
    private final Map<UUID, Location> cacheMap = new HashMap<>();
    public ViveCraftEyeLocation(VectorDisplays plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        TerminalManager.inst().getPlugin().getScheduler().runTaskTimerAsync(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                VRPose pose = VRAPI.instance().getVRPose(p);
                if (pose == null) continue;
                VRBodyPartData hand = pose.getBodyPartData(VRBodyPart.MAIN_HAND);
                if (hand == null) continue;

                Vector pos = hand.getPos();
                Location loc = new Location(p.getWorld(), pos.getX(), pos.getY(), pos.getZ());
                loc.setDirection(hand.getDir());

                cacheMap.put(p.getUniqueId(), loc);
            }
        }, 1L, 1L);
    }
    @EventHandler
    public void onPlayerOnline(PlayerJoinEvent e) {
        cacheMap.remove(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        cacheMap.remove(e.getPlayer().getUniqueId());
    }
    @Override
    public @NotNull Location getEyeLocation(@NotNull Player player) {
        Location cached = this.cacheMap.get(player.getUniqueId());
        if (cached != null) {
            return cached;
        }
        return DEFAULT.getEyeLocation(player);
    }
}
