package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.other.InteractionMeta;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * 基于交互实体实现的锚点实体
 * 通常用于设置负高度，将乘客原点降到玩家眼部
 */
@ApiStatus.Internal
public final class EntityMountAnchor extends AbstractEntity<EntityMountAnchor> {
    private final InteractionMeta meta;
    private final Player player;
    private float passengerOffset;

    public EntityMountAnchor(@NotNull DisplayRoot root) {
        super(RenderMode.VIEWER_LIST);
        this.player = root.getPlayer();
        this.meta = (InteractionMeta) EntityMeta.createMeta(entityID, EntityTypes.INTERACTION);
        setDisplayRoot(root);
        updatePassengerOffset();
        addViewer(player);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityTypes.INTERACTION;
    }

    public void refreshPassengerOffset() {
        if (updatePassengerOffset() && !isDead()) update();
    }

    private boolean updatePassengerOffset() {
        float offset = (float) (player.getEyeHeight() - player.getHeight());
        if (!Float.isFinite(offset)) {
            throw new IllegalArgumentException("玩家眼部骑乘偏移必须是有限值");
        }
        if (Float.compare(passengerOffset, offset) == 0) return false;
        passengerOffset = offset;
        return true;
    }

    @Override
    public @Nullable PacketWrapper<?> buildSpawnPacket() {
        if (location == null) return null;
        Location position = player.getLocation();
        return new WrapperPlayServerSpawnEntity(entityID, Optional.of(UUID.randomUUID()), EntityTypes.INTERACTION,
                new Vector3d(position.getX(), position.getY() + player.getHeight(), position.getZ()),
                0, 0, 0, 0, Optional.empty());
    }

    @Override
    protected @NotNull InteractionMeta createMeta() {
        applyCommonMeta(meta);
        meta.setInvisible(true);
        meta.setHasNoGravity(true);
        meta.setWidth(0);
        meta.setHeight(passengerOffset);
        meta.setResponsive(false);
        return meta;
    }
}
