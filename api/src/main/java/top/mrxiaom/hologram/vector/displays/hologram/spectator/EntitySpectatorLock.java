package top.mrxiaom.hologram.vector.displays.hologram.spectator;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.AbstractEntity;
import top.mrxiaom.hologram.vector.displays.hologram.IEntityIdProvider;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;

import java.util.Optional;
import java.util.UUID;

public class EntitySpectatorLock extends AbstractEntity<EntitySpectatorLock> {

    private final EntityType entityType;
    private final EntityMeta meta;
    private final Player player;

    public EntitySpectatorLock(IEntityIdProvider provider, Player player) {
        this(provider, EntityTypes.SKELETON, player);
    }

    public EntitySpectatorLock(IEntityIdProvider provider, EntityType entityType, Player player) {
        super(RenderMode.VIEWER_LIST, provider);
        this.entityType = entityType;
        setSilent(true);
        this.player = player;
        addViewer(player);
        this.meta = EntityMeta.createMeta(this.entityID, getEntityType());
    }

    public EntitySpectatorLock(Player player) {
        this(player, EntityTypes.SKELETON);
    }

    public EntitySpectatorLock(Player player, EntityType entityType) {
        this(IEntityIdProvider.DEFAULT, entityType, player);
    }

    public Player getPlayer() {
        return player;
    }

    public void updateSpectatorTarget() {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            sendPacket(player, new WrapperPlayServerCamera(entityID));
        }
    }

    public void ejectSpectator() {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            sendPacket(player, new WrapperPlayServerCamera(player.getEntityId()));
        }
    }

    @Override
    public void kill() {
        if (!dead) ejectSpectator();
        super.kill();
    }

    @Override
    protected EntityType getEntityType() {
        return entityType;
    }

    @Override
    public @Nullable PacketWrapper<?> buildSpawnPacket() {
        Location loc = this.location;
        if (loc == null) return null;
        Vector3d pos = new Vector3d(loc.getX(), loc.getY(), loc.getZ());
        return new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), getEntityType(),
                pos, loc.getPitch(), loc.getYaw(), loc.getYaw(), 0, Optional.empty()
        );
    }

    public EntitySpectatorLock teleport(@NotNull Location location, boolean rotateHead) {
        if (!location.equals(this.location)) {
            this.location = location;
            if (getEntityType() != null) {
                sendPacket(new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false));
                if (rotateHead) {
                    sendPacket(new WrapperPlayServerEntityHeadLook(this.entityID, location.getYaw()));
                }
            }
        }
        return this;
    }

    protected @Nullable EntityMeta createMeta() {
        applyCommonMeta(meta);
        meta.setInvisible(true);
        return meta;
    }
}
