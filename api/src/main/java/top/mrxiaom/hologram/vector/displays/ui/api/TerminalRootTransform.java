package top.mrxiaom.hologram.vector.displays.ui.api;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.hologram.DisplayRoot;
import top.mrxiaom.hologram.vector.displays.hologram.EntityDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityMountAnchor;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.utils.joml.Matrix4f;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 终端的玩家骑乘根坐标变换。
 * <p>
 * 客户端坐标为 +X 向屏幕右、+Y 向屏幕上、-Z 向视角前方。
 */
public final class TerminalRootTransform implements DisplayRoot {
    private final Terminal<?> terminal;
    private final Player player;
    private volatile Matrix4f matrix;
    private final Set<Integer> spawned = new LinkedHashSet<>();
    private final EntityMountAnchor mountAnchor;
    private IRunTask anchorTask;
    private boolean closed;

    TerminalRootTransform(Terminal<?> terminal, Player player, org.joml.Matrix4f matrix) {
        this.terminal = terminal;
        this.player = player;
        setMatrix(matrix);
        this.mountAnchor = new EntityMountAnchor(this);
    }

    void setMatrix(org.joml.Matrix4f matrix) {
        Matrix4f copy = new Matrix4f(matrix);
        if (!copy.isFinite() || copy.m03() != 0 || copy.m13() != 0
                || copy.m23() != 0 || copy.m33() != 1) {
            throw new IllegalArgumentException("根变换必须是有限的仿射矩阵");
        }
        this.matrix = copy;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull org.joml.Matrix4f getMatrix() {
        return matrix.toNative();
    }

    @Override
    public @NotNull Location getSpawnLocation() {
        return player.getLocation().add(0, player.getEyeHeight(), 0);
    }

    @Override
    public boolean isTranslationOnly() {
        Matrix4f m = matrix;
        return m.m00() == 1 && m.m11() == 1 && m.m22() == 1
                && m.m01() == 0 && m.m02() == 0 && m.m10() == 0
                && m.m12() == 0 && m.m20() == 0 && m.m21() == 0;
    }

    @Override
    public @NotNull Matrix4f transform(@NotNull EntityDisplay<?> entity) {
        Location origin = terminal.getLocation();
        Location position = entity.getLocation();
        if (position == null) position = origin;
        // 世界布局原点先归零；非固定定位控件、三角形和滑块的位置差也进入局部变换。
        return new Matrix4f(matrix).translate(
                (float) (position.getX() - origin.getX()),
                (float) (position.getY() - origin.getY()),
                (float) (position.getZ() - origin.getZ())
        ).mul(entity.getTransformationMatrixFixed());
    }

    @ApiStatus.Internal
    public @NotNull EntityMountAnchor getMountAnchor() {
        return mountAnchor;
    }

    @Override
    public synchronized void onSpawn(@NotNull EntityDisplay<?> entity) {
        if (closed || !spawned.add(entity.getEntityID())) return;
        if (mountAnchor.isDead()) {
            // 生成交互实体，设置负高度，再把它骑乘到玩家，最后挂载展示实体。
            mountAnchor.refreshPassengerOffset();
            HologramAPI.getHologram().spawn(mountAnchor, player.getLocation());
            HologramAPI.getPassengers().add(player, mountAnchor.getEntityID());
            anchorTask = HologramAPI.getHologram().getPlugin().getScheduler()
                    .runAtEntityTimer(player, this::refreshAnchor, 1L, 1L);
        }
        sendPassengers();
    }

    private synchronized void refreshAnchor() {
        if (!closed && !spawned.isEmpty() && player.isOnline()) {
            mountAnchor.refreshPassengerOffset();
        }
    }

    @Override
    public synchronized void onRemove(@NotNull EntityDisplay<?> entity) {
        if (!spawned.remove(entity.getEntityID())) return;
        sendPassengers();
        if (spawned.isEmpty()) removeAnchor();
    }

    private void sendPassengers() {
        if (!mountAnchor.isDead() && player.isOnline()) {
            HologramAPI.getPlayerManager().sendPacketSilently(player, new WrapperPlayServerSetPassengers(
                    mountAnchor.getEntityID(), spawned.stream().mapToInt(Integer::intValue).toArray()));
        }
    }

    private void removeAnchor() {
        if (anchorTask != null) {
            anchorTask.cancel();
            anchorTask = null;
        }
        HologramAPI.getPassengers().remove(player, mountAnchor.getEntityID());
        if (!mountAnchor.isDead()) {
            HologramAPI.getHologram().remove(mountAnchor);
            mountAnchor.removeAllViewers();
        }
    }

    synchronized void close() {
        closed = true;
        spawned.clear();
        sendPassengers();
        removeAnchor();
    }
}
