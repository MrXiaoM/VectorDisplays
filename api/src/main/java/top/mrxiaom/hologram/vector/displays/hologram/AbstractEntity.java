package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractEntity<This extends AbstractEntity<This>> {
    public static final LegacyComponentSerializer legacyText = LegacyComponentSerializer.legacySection();

    protected final PluginWrapper plugin;

    protected AbstractEntity<?> parent = null;

    protected double nearbyEntityScanningDistance = 40.0;

    protected int entityID;
    protected boolean glowing = false;
    protected boolean silent = true;
    protected EntityPose pose = EntityPose.STANDING;

    protected RenderMode renderMode;
    protected Location location;
    protected final Map<UUID, PlayerMeta> viewers = new ConcurrentHashMap<>();
    protected final Map<UUID, PlayerMeta> leftViewers = new ConcurrentHashMap<>();
    protected boolean dead = true;

    protected AbstractEntity(RenderMode renderMode) {
        this(renderMode, IEntityIdProvider.DEFAULT);
    }
    protected AbstractEntity(RenderMode renderMode, IEntityIdProvider provider) {
        PluginWrapper plugin = HologramAPI.getHologram().getPlugin();
        if (plugin == null) {
            throw new IllegalStateException("HologramAPI is not initialized!");
        }
        this.plugin = plugin;
        this.entityID = provider.nextId(getEntityType());
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

    /**
     * 请使用 <code>HologramAPI.getHologram().restartUpdateTimer(long)</code>
     * @see HologramManager#restartUpdateTimer(long)
     */
    @Deprecated(forRemoval = true)
    protected void startRunnable() {
    }

    /**
     * 请使用 <code>HologramAPI.getHologram().restartUpdateTimer(long)</code>
     * @see HologramManager#restartUpdateTimer(long)
     */
    @Deprecated(forRemoval = true)
    protected void startRunnable(long delay) {
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
        updateAffectedPlayers();
        sendSpawnPacket(packet);
        this.dead = false;
        update();
    }

    protected void sendSpawnPacket(PacketWrapper<?> packet) {
        sendPacket(packet);
    }

    protected void sendSpawnPacket(Player player, PacketWrapper<?> packet) {
        sendPacket(player, packet);
    }

    @Nullable
    protected abstract PacketWrapper<?> buildSpawnPacket();

    protected void applyCommonMeta(EntityMeta meta) {
        meta.setSilent(isSilent());
        meta.setGlowing(isGlowing());
        meta.setPose(getPose());
    }

    public void update() {
        updateAffectedPlayers();
        EntityMeta meta = createMeta();
        sendPacket(meta == null ? null : meta.createPacket());
    }

    @Nullable
    protected abstract EntityMeta createMeta();

    /**
     * Use HologramManager#remove(TextHologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        this.dead = true;
        this.location = null;
        if (getEntityType() != null) {
            sendPacket(new WrapperPlayServerDestroyEntities(this.entityID));
        }
    }

    public This teleport(@NotNull Location location) {
        // 在位置没有变化时，不进行传送
        if (!location.equals(this.location)) {
            this.location = location;
            if (getEntityType() != null) {
                sendPacket(new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false));
            }
        }
        return $this();
    }

    /**
     * 为实体添加/清除药水效果。注意，这个方法仅会发包，服务端不会储存药水效果状态
     * @see AbstractEntity#potion(PotionType, int, Integer, byte)
     */
    public This potion(@NotNull PotionType type, @Nullable Integer duration) {
        return potion(type, 1, duration, (byte) 0);
    }
    /**
     * 为实体添加/清除药水效果。注意，这个方法仅会发包，服务端不会储存药水效果状态
     * @see AbstractEntity#potion(PotionType, int, Integer, byte)
     */
    public This potion(@NotNull PotionType type, @Nullable Integer duration, byte flags) {
        return potion(type, 0, duration, flags);
    }
    /**
     * 为实体添加/清除药水效果。注意，这个方法仅会发包，服务端不会储存药水效果状态
     * @see AbstractEntity#potion(PotionType, int, Integer, byte)
     */
    public This potion(@NotNull PotionType type, @Range(from=0, to=Byte.MAX_VALUE) int amplifier, @Range(from=-1, to=Integer.MAX_VALUE) int duration) {
        return potion(type, amplifier, duration, (byte) 0);
    }
    /**
     * 为实体添加/清除药水效果。注意，这个方法仅会发包，服务端不会储存药水效果状态
     * @param type 药水效果类型
     * @param amplifier 效果放大器，客户端显示的等级是 <code>amplifier + 1</code>
     * @param duration 效果持续时间，设置 <code>-1</code> 代表无限时间，设置 <code>null</code> 则清除药水效果
     * @param flags 药水效果标记，可用标记如下，需要多个标记可使用与运算 (<code>&amp;</code>) 来合并
     * <ul>
     *     <li><code>FLAG_AMBIENT</code> = <code>0x01</code></li>
     *     <li><code>FLAG_VISIBLE</code> = <code>0x02</code></li>
     *     <li><code>FLAG_SHOW_ICONS</code> = <code>0x04</code></li>
     * </ul>
     */
    public This potion(
            @NotNull PotionType type,
            @Range(from=0, to=Byte.MAX_VALUE) int amplifier,
            @Nullable Integer duration,
            byte flags
    ) {
        if (getEntityType() != null) {
            if (duration == null) {
                sendPacket(new WrapperPlayServerRemoveEntityEffect(this.entityID, type));
            } else {
                int d = Math.max(-1, duration);
                sendPacket(new WrapperPlayServerEntityEffect(this.entityID, type, amplifier, d, flags));
            }
        }
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
        UUID uuid = player.getUniqueId();
        PlayerMeta meta = viewers.get(uuid);
        if (meta == null) {
            meta = new PlayerMeta(player);
            this.viewers.put(uuid, meta);
            respawn = true;
        }
        if (this.leftViewers.remove(uuid) != null) {
            respawn = true;
        }
        if (respawn && !dead && location != null) {
            respawnFor(player);
        }
        return $this();
    }

    public This addViewer(@NotNull PlayerMeta meta) {
        boolean respawn = false;
        UUID uuid = meta.player().getUniqueId();
        if (!viewers.containsKey(uuid)) {
            this.viewers.put(uuid, meta);
            respawn = true;
        }
        if (this.leftViewers.remove(uuid) != null) {
            respawn = true;
        }
        if (respawn && !dead && location != null) {
            respawnFor(meta.player());
        }
        return $this();
    }

    protected void respawnFor(@NotNull Player player) {
        PacketWrapper<?> packet = buildSpawnPacket();
        sendSpawnPacket(player, packet);
        updateAffectedPlayers();
        EntityMeta meta = createMeta();
        sendPacket(player, meta == null ? null : meta.createPacket());
    }

    public This removeViewer(@NotNull Player player) {
        this.viewers.remove(player.getUniqueId());
        this.leftViewers.remove(player.getUniqueId());
        if (!dead && getEntityType() != null) {
            sendPacket(player, new WrapperPlayServerDestroyEntities(this.entityID));
        }
        return $this();
    }

    public This removeAllViewers() {
        if (getEntityType() != null) {
            sendPacket(new WrapperPlayServerDestroyEntities(this.entityID));
        }
        this.viewers.clear();
        this.leftViewers.clear();
        return $this();
    }

    protected void updateAffectedPlayers() {
        updateAffectedPlayers(HologramAPI::shouldShowToNearby);
    }
    protected void updateAffectedPlayers(Predicate<Player> nearbyPlayerFilter) {
        if (this.dead || this.location == null) return;
        // 如果这个实体有父实体
        if (parent != null) {
            // 获取父实体的可视玩家列表
            List<Player> playerList = parent.getViewers();
            for (Player viewer : playerList) {
                // 将未添加的玩家添加进去
                if (viewers.containsKey(viewer.getUniqueId())) continue;
                if (viewer.isOnline()) {
                    addViewer(viewer);
                }
            }
            // 将不在父实体的可视玩家列表中的玩家移出去
            viewers.forEach((uuid, meta) -> {
                Player player = meta.player();
                if (!playerList.contains(player) || !player.isOnline()) {
                    removeViewer(player);
                }
            });
            return;
        }
        World world = this.location.getWorld();
        double viewDistance = nearbyEntityScanningDistance;
        viewers.forEach((uuid, meta) -> { // 超出可视范围自动销毁实体
            Player player = meta.player();
            if (player.isOnline() && (player.getWorld() != world || player.getLocation().distance(this.location) > viewDistance)) {
                if (this.renderMode == RenderMode.NEARBY) {
                    removeViewer(player);
                } else {
                    if (getEntityType() != null) {
                        sendPacket(player, new WrapperPlayServerDestroyEntities(this.entityID));
                    }
                    if (this.renderMode == RenderMode.VIEWER_LIST && !leftViewers.containsKey(uuid)) {
                        leftViewers.put(uuid, meta);
                    }
                }
            }
            if (!player.isOnline()) {
                removeViewer(player);
            }
        });

        if (this.renderMode == RenderMode.VIEWER_LIST) {
            // 将回到可视范围的玩家添加回来
            leftViewers.forEach((uuid, meta) -> {
                Player player = meta.player();
                if (player.isOnline() && player.getWorld() == world && player.getLocation().distance(this.location) <= viewDistance) {
                    addViewer(meta);
                }
            });
            return;
        }

        if (this.renderMode == RenderMode.NEARBY && world != null) {
            // 将附近的玩家添加进来
            for (Player player : world.getPlayers()) {
                if (player.getLocation().distance(this.location) > viewDistance) continue;
                if (!viewers.containsKey(player.getUniqueId()) && nearbyPlayerFilter.test(player)) {
                    addViewer(player);
                }
            }
        }
    }

    protected void sendPacket(@Nullable PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE || packet == null) return;
        viewers.forEach((uuid, meta) -> sendPacket(meta, packet));
    }

    protected void sendPacket(@NotNull PlayerMeta meta, @Nullable PacketWrapper<?> packet) {
        Player player = meta.player();
        if (packet != null && player.isOnline()) {
            PacketWrapper<?> finalPacket = meta.processPacket(packet);
            HologramAPI.getPlayerManager().sendPacket(player, finalPacket);
        }
    }

    protected void sendPacket(@NotNull Player player, @Nullable PacketWrapper<?> packet) {
        if (packet != null && player.isOnline()) {
            PlayerMeta meta = viewers.get(player.getUniqueId());
            if (meta != null) {
                PacketWrapper<?> finalPacket = meta.processPacket(packet);
                HologramAPI.getPlayerManager().sendPacket(player, finalPacket);
            } else {
                HologramAPI.getPlayerManager().sendPacket(player, packet);
            }
        }
    }

    /**
     * @see HologramManager#getUpdatePeriod()
     */
    @Deprecated(forRemoval = true)
    public long getUpdateTaskPeriod() {
        return 3 * 20L;
    }

    public double getNearbyEntityScanningDistance() {
        return nearbyEntityScanningDistance;
    }

    /**
     * 请使用 <code>HologramAPI.getHologram().restartUpdateTimer(long)</code>
     * @see HologramManager#restartUpdateTimer(long)
     */
    @Deprecated(forRemoval = true)
    public This setUpdateTaskPeriod(long updateTaskPeriod) {
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

    public boolean isGlowing() {
        return glowing;
    }

    public This setGlowing(boolean glowing) {
        this.glowing = glowing;
        return $this();
    }

    public boolean isSilent() {
        return silent;
    }

    public This setSilent(boolean silent) {
        this.silent = silent;
        return $this();
    }

    @NotNull
    public EntityPose getPose() {
        return pose;
    }

    public This setPose(@Nullable EntityPose pose) {
        this.pose = pose == null ? EntityPose.STANDING : pose;
        return $this();
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
        List<Player> list = new ArrayList<>();
        viewers.forEach((uuid, meta) -> list.add(meta.player()));
        return list;
    }

    public boolean isDead() {
        return dead;
    }

    @Nullable
    @Deprecated
    public IRunTask getTask() {
        return null;
    }

    public static com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

}
