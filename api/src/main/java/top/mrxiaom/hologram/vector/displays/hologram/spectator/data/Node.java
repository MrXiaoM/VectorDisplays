package top.mrxiaom.hologram.vector.displays.hologram.spectator.data;

import org.bukkit.Location;

import java.util.Objects;

/**
 * 摄像机路径点
 */
public class Node {
    private final long timelineMills;
    private final double x, y, z;
    private final double vX, vY, vZ;

    private Node(long timelineMills, double x, double y, double z, double vX, double vY, double vZ) {
        this.timelineMills = timelineMills;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vX = vX;
        this.vY = vY;
        this.vZ = vZ;
    }

    /**
     * 时间进度（毫秒）
     */
    public long timelineMills() {
        return timelineMills;
    }

    /**
     * 相对曲线动画原点位置 x 坐标
     */
    public double x() {
        return x;
    }

    /**
     * 相对曲线动画原点位置 y 坐标
     */
    public double y() {
        return y;
    }

    /**
     * 相对曲线动画原点位置 z 坐标
     */
    public double z() {
        return z;
    }

    /**
     * 摄像机朝向的方向向量 x 分量
     */
    public double vX() {
        return vX;
    }

    /**
     * 摄像机朝向的方向向量 y 分量
     */
    public double vY() {
        return vY;
    }

    /**
     * 摄像机朝向的方向向量 z 分量
     */
    public double vZ() {
        return vZ;
    }

    /**
     * 创建一个新的路径点。
     * <p>
     * 在 xz 平面，绕 y 轴，按 z 轴正方向的右侧旋转指定角度（即绕 y 轴顺时针旋转）
     * @param yaw 旋转角度，使用角度制
     * @return 新的路径点实例
     */
    public Node rotateYaw(float yaw) {
        double radians = Math.toRadians(yaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double newX = x * cos + z * sin;
        double newZ = z * cos - x * sin;
        double newVX = vX * cos + vZ * sin;
        double newVZ = vZ * cos - vX * sin;
        return of(timelineMills, newX, y, newZ, newVX, vY, newVZ);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node node)) return false;
        return timelineMills == node.timelineMills && Double.compare(x, node.x) == 0 && Double.compare(y, node.y) == 0 && Double.compare(z, node.z) == 0 && Double.compare(vX, node.vX) == 0 && Double.compare(vY, node.vY) == 0 && Double.compare(vZ, node.vZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timelineMills, x, y, z, vX, vY, vZ);
    }

    public static Node of(long timelineMills, Location loc) {
        return of(timelineMills, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static Node of(long timelineMills, Location loc, float yaw, float pitch) {
        return of(timelineMills, loc.getX(), loc.getY(), loc.getZ(), yaw, pitch);
    }

    public static Node of(long timelineMills, Location loc, double vX, double vY, double vZ) {
        return of(timelineMills, loc.getX(), loc.getY(), loc.getZ(), vX, vY, vZ);
    }

    public static Node of(long timelineMills, double x, double y, double z, float yaw, float pitch) {
        double vY = -Math.sin(Math.toRadians(pitch));
        double xz = Math.cos(Math.toRadians(pitch));
        double vX = -xz * Math.sin(Math.toRadians(yaw));
        double vZ = xz * Math.cos(Math.toRadians(yaw));
        return of(timelineMills, x, y, z, vX, vY, vZ);
    }

    public static Node of(long timelineMills, double x, double y, double z, double vX, double vY, double vZ) {
        return new Node(timelineMills, x, y, z, vX, vY, vZ);
    }
}
