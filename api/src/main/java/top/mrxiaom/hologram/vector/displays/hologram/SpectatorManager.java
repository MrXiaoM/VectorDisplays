package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSpectate;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSpectateEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectatorManager extends PacketListenerAbstract implements Listener {
    private final PacketEventsAPI<?> api;
    private final HologramManager hologramManager;
    private final Map<UUID, EntitySpectatorLock> spectatorLocks = new HashMap<>();
    public SpectatorManager(PluginWrapper plugin, HologramManager hologramManager) {
        this.hologramManager = hologramManager;
        this.api = PacketEvents.getAPI();
        this.api.getEventManager().registerListener(this);
        Bukkit.getPluginManager().registerEvents(this, plugin.getPlugin());
    }

    @Nullable
    public EntitySpectatorLock getSpectator(@NotNull UUID playerId) {
        return spectatorLocks.get(playerId);
    }

    public void unlockSpectator(@NotNull Player player) {
        EntitySpectatorLock entity = spectatorLocks.remove(player.getUniqueId());
        if (entity != null) {
            hologramManager.remove(entity);
        }
    }

    public void lockSpectator(@NotNull Player player) {
        lockSpectator(player, EntityTypes.SKELETON);
    }

    public void lockSpectator(@NotNull Player player, EntityType entityType) {
        unlockSpectator(player);
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            EntitySpectatorLock entity = new EntitySpectatorLock(player, entityType);
            Location location = player.getLocation().clone();
            location.setYaw(player.getEyeLocation().getYaw());
            hologramManager.spawn(entity, location);
            entity.updateSpectatorTarget();
            spectatorLocks.put(player.getUniqueId(), entity);
        }
    }

    private void onPlayerLeaveSpectate(Player player, PacketReceiveEvent event) {
        EntitySpectatorLock lock = spectatorLocks.get(player.getUniqueId());
        if (lock != null) {
            event.setCancelled(true);
            lock.updateSpectatorTarget();
        }
    }

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.SPECTATE)) {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                WrapperPlayClientSpectate packet = new WrapperPlayClientSpectate(event);
                if (player.getUniqueId() == packet.getTargetUUID()) {
                    onPlayerLeaveSpectate(player, event);
                }
            }
            return;
        }
        if (event.getPacketType().equals(PacketType.Play.Client.SPECTATE_ENTITY)) {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                WrapperPlayClientSpectateEntity packet = new WrapperPlayClientSpectateEntity(event);
                if (player.getEntityId() == packet.getEntityId()) {
                    onPlayerLeaveSpectate(player, event);
                }
            }
            return;
        }
        super.onPacketReceive(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMode(PlayerGameModeChangeEvent e) {
        if (e.isCancelled()) return;
        if (!e.getNewGameMode().equals(GameMode.SPECTATOR)) {
            unlockSpectator(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        unlockSpectator(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        unlockSpectator(e.getPlayer());
    }

    protected void onDisable() {
        HandlerList.unregisterAll(this);
        this.api.getEventManager().unregisterListener(this);
        spectatorLocks.clear();
    }

}
