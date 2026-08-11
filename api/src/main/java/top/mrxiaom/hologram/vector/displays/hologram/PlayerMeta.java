package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMeta {
    private final Player player;
    private final ConcurrentHashMap<Integer, EntityData<?>> metadataMap = new ConcurrentHashMap<>();

    public PlayerMeta(Player player) {
        this.player = player;
    }

    public Player player() {
        return player;
    }

    @SuppressWarnings("unchecked")
    public <T> T getIndex(int index, @Nullable T defaultValue) {
        EntityData<?> value = this.metadataMap.get(index);
        return value != null ? (T) value.getValue() : defaultValue;
    }

    public <T> void setIndex(int index, @NotNull EntityDataType<T> dataType, T value) {
        final EntityData<?> entry = new EntityData<>(index, dataType, value);
        this.metadataMap.put(index, entry);
    }

    @NotNull
    public PacketWrapper<?> processPacket(PacketWrapper<?> oldPacket) {
        if (oldPacket instanceof WrapperPlayServerEntityMetadata packet && !metadataMap.isEmpty()) {
            List<EntityData<?>> newMetadata = new ArrayList<>();
            Set<Integer> handledIndex = new HashSet<>();
            for (EntityData<?> oldData : packet.getEntityMetadata()) {
                int index = oldData.getIndex();
                handledIndex.add(index);
                newMetadata.add(metadataMap.getOrDefault(index, oldData));
            }
            metadataMap.forEach((index, data) -> {
                if (!handledIndex.contains(index)) {
                    newMetadata.add(data);
                }
            });
            packet.setEntityMetadata(newMetadata);
        }
        return oldPacket;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerMeta meta)) return false;
        return Objects.equals(player.getUniqueId(), meta.player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player.getUniqueId());
    }
}
