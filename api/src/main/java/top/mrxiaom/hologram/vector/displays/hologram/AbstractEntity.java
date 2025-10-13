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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.ArrayList;
import java.util.List;

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
    protected final List<Player> viewers = new ArrayList<>();
    protected final List<Player> leftViewers = new ArrayList<>();
    protected boolean dead = true;
    protected IRunTask task;

    protected AbstractEntity(RenderMode renderMode) {
        PluginWrapper plugin = HologramAPI.getHologram().getPlugin();
        if (plugin == null) {
            throw new IllegalStateException("HologramAPI is not initialized!");
        }
        this.plugin = plugin;
        // TODO: 使用更好的方法生成实体ID
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
        PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.viewers.clear();
        this.leftViewers.clear();
        return $this();
    }

    private void updateAffectedPlayers() {
        if (this.location == null) return;
        // 如果这个实体有父实体
        if (parent != null) {
            // 获取父实体的可视玩家列表
            List<Player> playerList = parent.getViewers();
            for (Player viewer : playerList) {
                // 将未添加的玩家添加进去
                if (viewers.contains(viewer)) continue;
                addViewer(viewer);
            }
            // 将不在父实体的可视玩家列表中的玩家移出去
            for (Object o : viewers.toArray()) {
                Player player = (Player) o;
                if (!playerList.contains(player)) {
                    removeViewer(player);
                }
            }
            return;
        }
        World world = this.location.getWorld();
        double viewDistance = nearbyEntityScanningDistance;
        for (Object o : viewers.toArray()) { // 超出可视范围自动销毁实体
            Player player = (Player) o;
            if (player.isOnline() && (player.getWorld() != world || player.getLocation().distance(this.location) > viewDistance)) {
                if (this.renderMode == RenderMode.NEARBY) {
                    removeViewer(player);
                } else {
                    PacketWrapper<?> packet = new WrapperPlayServerDestroyEntities(this.entityID);
                    sendPacket(player, packet);
                    if (this.renderMode == RenderMode.VIEWER_LIST && !leftViewers.contains(player)) {
                        leftViewers.add(player);
                    }
                }
            }
        }

        if (this.renderMode == RenderMode.VIEWER_LIST) {
            // 将回到可视范围的玩家添加回来
            for (Object o : leftViewers.toArray()) {
                Player player = (Player) o;
                if (player.isOnline() && player.getWorld() == world && player.getLocation().distance(this.location) <= viewDistance) {
                    addViewer(player);
                }
            }
            return;
        }

        if (this.renderMode == RenderMode.NEARBY && world != null) {
            // 将附近的玩家添加进来
            for (Player player : world.getPlayers()) {
                if (player.getLocation().distance(this.location) > viewDistance) continue;
                if (!this.viewers.contains(player)) {
                    addViewer(player);
                }
            }
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
