package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractEntity<This extends AbstractEntity<This>> {
    public static final LegacyComponentSerializer legacyText = LegacyComponentSerializer.legacySection();

    protected final PluginWrapper plugin;

    protected AbstractEntity<?> parent = null;

    protected long updateTaskPeriod = 20L * 3;
    protected double nearbyEntityScanningDistance = 40.0;

    protected WrapperEntity entity;
    protected int entityID;

    protected RenderMode renderMode;
    protected Location location;
    protected final List<Player> viewers = new CopyOnWriteArrayList<>();
    protected final List<Player> leftViewers = new CopyOnWriteArrayList<>();
    protected boolean dead = true;
    protected IRunTask task;

    protected AbstractEntity(RenderMode renderMode) {
        PluginWrapper plugin = HologramAPI.getHologram().getPlugin();
        if (plugin == null) {
            throw new IllegalStateException("HologramAPI is not initialized!");
        }
        this.plugin = plugin;
        this.entity = new WrapperEntity(getEntityType());
        this.entityID = entity.getEntityId();
        this.renderMode = renderMode;
    }

    public void setParent(@Nullable AbstractEntity<?> parent) {
        this.parent = parent;
    }

    @ApiStatus.Internal
    public void setRenderMode(@NotNull RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    @SuppressWarnings({"unchecked"})
    protected This $this() {
        return (This) this;
    }

    protected void startRunnable() {
        if (task != null) return;
        startRunnable(20L);
    }

    protected void startRunnable(long delay) {
        if (task != null) return;
        task = plugin.getScheduler().runTaskTimer(this::updateAffectedPlayers, delay, updateTaskPeriod);
    }

    protected abstract EntityType getEntityType();

    /**
     * Use HologramManager#spawn(TextHologram.class, Location.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void spawn(@NotNull Location location) {
        if (!dead) kill();
        this.location = location;
        PacketWrapper<?> packet = buildSpawnPacket();
        plugin.getScheduler().runTask(() -> {
            updateAffectedPlayers();
            sendPacket(packet);
            this.dead = false;
            update();
        });
    }

    protected abstract PacketWrapper<?> buildSpawnPacket();


    public void update() {
        plugin.getScheduler().runTask(() -> {
            updateAffectedPlayers();
            EntityMeta meta = createMeta();
            sendPacket(meta.createPacket());
        });
    }

    protected abstract EntityMeta createMeta();

    /**
     * Use HologramManager#remove(TextHologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
        this.location = null;
    }

    public This teleport(@NotNull Location location) {
        PacketWrapper<?> packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return $this();
    }

    public This addAllViewers(@NotNull List<Player> viewerList) {
        for (Player player : viewerList) {
            addViewer(player);
        }
        return $this();
    }

    public This addViewer(@NotNull Player player) {
        boolean respawn = false;
        if (!viewers.contains(player)) {
            this.viewers.add(player);
            respawn = true;
        }
        if (this.leftViewers.remove(player)) {
            respawn = true;
        }
        if (respawn && !dead) {
            respawnFor(player);
        }
        return $this();
    }

    private void respawnFor(@NotNull Player player) {
        PacketWrapper<?> packet = buildSpawnPacket();
        sendPacket(player, packet);
        plugin.getScheduler().runTask(() -> {
            updateAffectedPlayers();
            EntityMeta meta = createMeta();
            sendPacket(player, meta.createPacket());
        });
    }

    public This removeViewer(@NotNull Player player) {
        this.viewers.remove(player);
        this.leftViewers.remove(player);
        if (!dead) {
            PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
            sendPacket(player, packet);
        }
        return $this();
    }

    public This removeAllViewers() {
        this.viewers.clear();
        this.leftViewers.clear();
        PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        return $this();
    }

    private void updateAffectedPlayers() {
        if (this.location == null) return;
        if (parent != null) {
            List<Player> playerList = parent.getViewers();
            for (Player viewer : playerList) {
                if (viewers.contains(viewer)) continue;
                addViewer(viewer);
            }
            for (Player viewer : new ArrayList<>(viewers)) {
                if (!playerList.contains(viewer)) {
                    removeViewer(viewer);
                }
            }
            return;
        }
        double viewDistance = renderMode.equals(RenderMode.NEARBY)
                ? nearbyEntityScanningDistance
                : 32.0;
        new ArrayList<>(viewers).stream() // 超出可视范围自动销毁实体
                .filter(player -> player.isOnline() && (player.getWorld() != this.location.getWorld() || player.getLocation().distance(this.location) > viewDistance))
                .forEach(player -> {
                    if (this.renderMode == RenderMode.NEARBY) {
                        removeViewer(player);
                    } else {
                        PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
                        sendPacket(player, packet);
                        if (this.renderMode == RenderMode.VIEWER_LIST && !leftViewers.contains(player)) {
                            leftViewers.add(player);
                        }
                    }
                });

        if (this.renderMode == RenderMode.VIEWER_LIST) {
            new ArrayList<>(leftViewers).stream() // 回到可视范围自动恢复实体
                    .filter(player -> player.isOnline() && player.getWorld() == this.location.getWorld() && player.getLocation().distance(this.location) <= viewDistance)
                    .forEach(this::addViewer);
            return;
        }

        if (this.renderMode == RenderMode.NEARBY && this.location.getWorld() != null) {
            this.location.getWorld().getNearbyEntities(this.location, viewDistance, viewDistance, viewDistance)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(entity -> addViewer((Player) entity));
        }
    }

    private void sendPacket(@NotNull PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE) return;
        viewers.forEach(player -> sendPacket(player, packet));
    }

    private void sendPacket(@NotNull Player player, @NotNull PacketWrapper<?> packet) {
        HologramAPI.getPlayerManager().sendPacket(player, packet);
    }

    public long getUpdateTaskPeriod() {
        return updateTaskPeriod;
    }

    public double getNearbyEntityScanningDistance() {
        return nearbyEntityScanningDistance;
    }

    public This setUpdateTaskPeriod(long updateTaskPeriod) {
        if (updateTaskPeriod <= 0) {
            throw new IllegalArgumentException("updateTaskPeriod can't equals or less than zero!");
        }
        this.updateTaskPeriod = updateTaskPeriod;
        if (task != null) {
            task.cancel();
            task = null;
            startRunnable(updateTaskPeriod);
        }
        return $this();
    }

    public This setNearbyEntityScanningDistance(double nearbyEntityScanningDistance) {
        if (nearbyEntityScanningDistance <= 0) {
            throw new IllegalArgumentException("nearbyEntityScanningDistance can't equals or less than zero!");
        }
        this.nearbyEntityScanningDistance = nearbyEntityScanningDistance;
        return $this();
    }

    public int getEntityID() {
        return entityID;
    }

    @NotNull
    public RenderMode getRenderMode() {
        return renderMode;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @NotNull
    public List<Player> getViewers() {
        return viewers;
    }

    public boolean isDead() {
        return dead;
    }

    @Nullable
    public IRunTask getTask() {
        return task;
    }

    public static com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

}
