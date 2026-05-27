package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class EntitySpectatorLock extends EntityDisplay<EntitySpectatorLock> {

    private final TextDisplayMeta meta;
    private final Player player;

    public EntitySpectatorLock(IEntityIdProvider provider, Player player) {
        super(RenderMode.VIEWER_LIST, provider);
        this.player = player;
        addViewer(player);
        this.meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, getEntityType());
    }

    public EntitySpectatorLock(Player player) {
        this(IEntityIdProvider.DEFAULT, player);
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
        return EntityTypes.TEXT_DISPLAY;
    }

    @Override
    public @Nullable PacketWrapper<?> buildSpawnPacket() {
        Location loc = this.location;
        if (loc == null) return null;
        Vector3d pos = new Vector3d(loc.getX(), loc.getY(), loc.getZ());
        return new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), getEntityType(),
                pos, loc.getPitch(), loc.getYaw(), 0f, 0, Optional.empty()
        );
    }

    protected @Nullable TextDisplayMeta createMeta() {
        applyCommonMeta(meta);
        applyDisplayMeta(meta);
        meta.setText(Component.empty());
        meta.setInvisible(true);
        return meta;
    }

    @Override
    public void spawn(@NotNull Location location) {
        _spawn(location);
    }

    @Override
    public EntitySpectatorLock teleport(@NotNull Location location) {
        return _teleport(location);
    }
}
