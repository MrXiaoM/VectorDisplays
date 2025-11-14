package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;

@FunctionalInterface
public interface IEntityIdProvider {
    IEntityIdProvider DEFAULT = NMS::nextEntityId;

    int nextId(@NotNull EntityType type);
}
