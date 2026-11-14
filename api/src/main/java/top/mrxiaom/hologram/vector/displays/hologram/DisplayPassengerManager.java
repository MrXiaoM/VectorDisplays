package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class DisplayPassengerManager extends PacketListenerAbstract {
    private final Map<UUID, Passengers> players = new ConcurrentHashMap<>();

    public void add(@NotNull Player player, int entityId) {
        Passengers passengers = players.computeIfAbsent(player.getUniqueId(), id -> new Passengers(player));
        synchronized (passengers) {
            if (passengers.virtual.add(entityId)) send(passengers);
        }
    }

    public void remove(@NotNull Player player, int entityId) {
        Passengers passengers = players.get(player.getUniqueId());
        if (passengers == null) return;
        synchronized (passengers) {
            if (!passengers.virtual.remove(entityId)) return;
            send(passengers);
            if (passengers.virtual.isEmpty()) players.remove(player.getUniqueId(), passengers);
        }
    }

    private void send(Passengers passengers) {
        if (passengers.player.isOnline()) {
            HologramAPI.getPlayerManager().sendPacketSilently(passengers.player,
                    new WrapperPlayServerSetPassengers(passengers.vehicleId, passengers.merged()));
        }
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_PASSENGERS) return;
        Player player = event.getPlayer();
        if (player == null) return;
        Passengers passengers = players.get(player.getUniqueId());
        if (passengers == null) return;
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
        if (packet.getEntityId() != passengers.vehicleId) return;
        synchronized (passengers) {
            // 服务端更新真实乘客时，保留本插件的虚拟乘客
            passengers.real = Arrays.stream(packet.getPassengers())
                    .filter(id -> !passengers.virtual.contains(id)).toArray();
            packet.setPassengers(passengers.merged());
            event.markForReEncode(true);
        }
    }

    public void clear(@NotNull Player player) {
        players.remove(player.getUniqueId());
    }

    public void close() {
        for (Passengers passengers : players.values()) {
            synchronized (passengers) {
                passengers.virtual.clear();
                send(passengers);
            }
        }
        players.clear();
    }

    private static final class Passengers {
        private final Player player;
        private final int vehicleId;
        private final Set<Integer> virtual = new LinkedHashSet<>();
        private int[] real;

        private Passengers(Player player) {
            this.player = player;
            this.vehicleId = player.getEntityId();
            this.real = player.getPassengers().stream().mapToInt(Entity::getEntityId).toArray();
        }

        private int[] merged() {
            return java.util.stream.IntStream.concat(Arrays.stream(real), virtual.stream().mapToInt(Integer::intValue))
                    .distinct().toArray();
        }
    }
}
