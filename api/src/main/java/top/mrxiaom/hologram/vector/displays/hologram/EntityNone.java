package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.tofaa.entitylib.meta.EntityMeta;
import org.jetbrains.annotations.Nullable;

/**
 * 不向玩家发包的占位用虚拟实体
 */
public class EntityNone extends AbstractEntity<EntityNone> {
    public EntityNone(RenderMode renderMode) {
        super(renderMode, type -> -1);
    }

    @Override
    protected EntityType getEntityType() {
        return null;
    }

    @Override
    protected @Nullable PacketWrapper<?> buildSpawnPacket() {
        return null;
    }

    @Override
    protected @Nullable EntityMeta createMeta() {
        return null;
    }
}
