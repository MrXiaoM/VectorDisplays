package top.mrxiaom.hologram.vector.displays.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Math;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;

public class QuaternionUtils {
    /**
     * 将 Minecraft 格式的欧拉角 (YXZ 顺序) 转换为四元数
     * @see org.joml.Quaternionf#rotationYXZ(float, float, float)
     */
    public static float[] fromEulerYXZtoQuaternion(float yaw, float pitch, float roll) {
        float yawRad = Math.toRadians(yaw);
        float pitchRad = Math.toRadians(pitch);
        float rollRad = Math.toRadians(roll);
        float sx = Math.sin(pitchRad * 0.5F);
        float cx = Math.cosFromSin(sx, pitchRad * 0.5F);
        float sy = Math.sin(yawRad * 0.5F);
        float cy = Math.cosFromSin(sy, yawRad * 0.5F);
        float sz = Math.sin(rollRad * 0.5F);
        float cz = Math.cosFromSin(sz, rollRad * 0.5F);
        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        return new float[] {
                x * cz + y * sz,
                y * cz - x * sz,
                w * sz - z * cz,
                w * cz + z * sz
        };
    }

    // 四元数乘法
    public static float[] multiplyF(float[] a, float[] b) {
        float w = a[3] * b[3] - a[0] * b[0] - a[1] * b[1] - a[2] * b[2];
        float x = a[3] * b[0] + a[0] * b[3] + a[1] * b[2] - a[2] * b[1];
        float y = a[3] * b[1] - a[0] * b[2] + a[1] * b[3] + a[2] * b[0];
        float z = a[3] * b[2] + a[0] * b[1] - a[1] * b[0] + a[2] * b[3];
        return new float[] { x, y, z, w };
    }

    static class Quaternion {
        final double x, y, z, w;
        Quaternion(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
        Quaternion(float[] rotation) {
            this(rotation[0], rotation[1], rotation[2], rotation[3]);
        }
        Quaternion conjugate() {
            return new Quaternion(-x, -y, -z, w);
        }
        Quaternion multiply(Quaternion other) {
            double newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
            double newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
            double newY = this.w * other.y - this.x * other.z + this.y * other.w + this.z * other.x;
            double newZ = this.w * other.z + this.x * other.y - this.y * other.x + this.z * other.w;
            return new Quaternion(newX, newY, newZ, newW);
        }
    }
    static class Point3D {
        final double x, y, z;
        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        Point3D(double[] point) {
            this(point[0], point[1], point[2]);
        }
        Point3D(float[] point) {
            this(point[0], point[1], point[2]);
        }
        Point3D(Location loc) {
            this(loc.getX(), loc.getY(), loc.getZ());
        }
        Point3D add(Point3D other) {
            return new Point3D(x + other.x, y + other.y, z + other.z);
        }
        Point3D subtract(Point3D other) {
            return new Point3D(x - other.x, y - other.y, z - other.z);
        }
        Location toLocation(World world) {
            return new Location(world, x, y, z);
        }
        float[] toRaw() {
            return new float[] { (float)x, (float)y, (float)z };
        }
        double[] toRawDouble() {
            return new double[] { x, y, z };
        }
    }

    /**
     * 作出旋转悬浮字后的坐标变换
     *
     * @param terminal 终端面板
     * @param location 子元素坐标
     * @return 变换后坐标
     */
    public static Location rotateChildren(Terminal<?> terminal, Location location) {
        return rotateChildren(terminal.getLocation(), terminal.getRotation(), location);
    }
    /**
     * 作出旋转悬浮字后的坐标变换 (豆包AI 生成)
     *
     * @param origin 旋转原点
     * @param rotate 旋转四元数
     * @param location 子元素坐标
     * @return 变换后坐标
     */
    public static Location rotateChildren(Location origin, float[] rotate, Location location) {
        Point3D pointA = new Point3D(location);  // 点A的坐标
        Point3D pointO = new Point3D(origin);  // 旋转中心O的坐标
        return rotateChildren(pointO, rotate, pointA).toLocation(location.getWorld());
    }

    /**
     * @see QuaternionUtils#rotateChildren(Location, float[], Location)
     */
    public static double[] rotateChildrenToDouble(Location origin, float[] rotate, double[] location) {
        Point3D pointA = new Point3D(location);  // 点A的坐标
        Point3D pointO = new Point3D(origin);  // 旋转中心O的坐标
        return rotateChildren(pointO, rotate, pointA).toRawDouble();
    }
    /**
     * 作出旋转悬浮字后的坐标变换 (豆包AI 生成)
     *
     * @param origin 旋转原点
     * @param rotate 旋转四元数
     * @param location 子元素坐标
     * @return 变换后坐标
     */
    public static float[] rotateChildren(float[] origin, float[] rotate, float[] location) {
        Point3D pointA = new Point3D(location);  // 点A的坐标
        Point3D pointO = new Point3D(origin);  // 旋转中心O的坐标
        Point3D rotated = rotateChildren(pointO, rotate, pointA);
        return rotated.toRaw();
    }
    /**
     * 作出旋转悬浮字后的坐标变换 (豆包AI 生成)
     *
     * @param pointO 旋转原点
     * @param rotate 旋转四元数
     * @param pointA 子元素坐标
     * @return 变换后坐标
     */
    private static Point3D rotateChildren(Point3D pointO, float[] rotate, Point3D pointA) {
        // 旋转四元数 (x, y, z, w)
        Quaternion rotation = new Quaternion(rotate);

        // 绕指定点旋转点
        // 1. 将点平移到以旋转中心为原点的坐标系
        Point3D translated = pointA.subtract(pointO);

        // 2. 绕原点旋转
        //   (1) 将点转换为纯四元数 (0, x, y, z)
        Quaternion pointQuaternion = new Quaternion(translated.x, translated.y, translated.z, 0);
        //   (2) 应用四元数旋转: q * p * q⁻¹
        Quaternion rotatedQuaternion = rotation.multiply(pointQuaternion).multiply(rotation.conjugate());
        //   (3) 提取旋转后的点坐标
        Point3D rotated = new Point3D(rotatedQuaternion.x, rotatedQuaternion.y, rotatedQuaternion.z);

        // 3. 平移回原坐标系，这就是旋转后的坐标
        return rotated.add(pointO);
    }

    public static float[] calculateRotation(
            float[] A, float[] B, float[] C, float[] D
    ) {
        float[] result = new float[4];
        calculateRotationQuaternion(result, A, B, C, D);
        return result;
    }

    // 从旋转轴和角度（弧度）创建四元数（x, y, z, w顺序）
    private static void fromAxisAngle(float[] result, float[] axis, float angle) {
        float[] normalizedAxis = new float[3];

        float len = Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
        if (len < 1e-6f) {
            throw new ArithmeticException("Cannot normalize zero vector");
        }
        float invLen = 1.0f / len;
        normalizedAxis[0] = axis[0] * invLen;
        normalizedAxis[1] = axis[1] * invLen;
        normalizedAxis[2] = axis[2] * invLen;

        float halfAngle = angle * 0.5f;
        float sinHalfAngle = Math.sin(halfAngle);
        float cosHalfAngle = Math.cos(halfAngle);

        // 四元数按x, y, z, w顺序存储
        result[0] = normalizedAxis[0] * sinHalfAngle;
        result[1] = normalizedAxis[1] * sinHalfAngle;
        result[2] = normalizedAxis[2] * sinHalfAngle;
        result[3] = cosHalfAngle;
    }

    // 计算将线段AB旋转到CD的四元数 (豆包AI 生成)
    private static void calculateRotationQuaternion(
            float[] result,  // 输出四元数 (x, y, z, w)
            float[] A, float[] B,  // 原始线段端点
            float[] C, float[] D)  // 旋转后线段端点
    {
        // 计算线段方向向量
        float[] AB = new float[3];
        AB[0] = B[0] - A[0];
        AB[1] = B[1] - A[1];
        AB[2] = B[2] - A[2];

        float[] CD = new float[3];
        CD[0] = D[0] - C[0];
        CD[1] = D[1] - C[1];
        CD[2] = D[2] - C[2];

        // 计算旋转轴：两方向向量的叉积
        float[] axis = new float[3];
        axis[0] = AB[1] * CD[2] - AB[2] * CD[1];
        axis[1] = AB[2] * CD[0] - AB[0] * CD[2];
        axis[2] = AB[0] * CD[1] - AB[1] * CD[0];

        float dotProduct = AB[0] * CD[0] + AB[1] * CD[1] + AB[2] * CD[2];

        // 处理共线情况（叉积为零向量）
        if (Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]) < 1e-6f) {
            // 如果方向相同，不需要旋转
            if (dotProduct > 0) {
                result[0] = 0;
                result[1] = 0;
                result[2] = 0;
                result[3] = 1;
            } else {
                // 如果方向相反，绕垂直于AB的任意轴旋转180度
                // 找一个垂直于AB的向量作为旋转轴
                if (Math.abs(AB[0]) > Math.abs(AB[1])) {
                    axis[0] = -AB[2];
                    axis[1] = 0;
                    axis[2] = AB[0];
                } else {
                    axis[0] = 0;
                    axis[1] = AB[2];
                    axis[2] = -AB[1];
                }
                fromAxisAngle(result, axis, (float) Math.PI);
            }
            return;
        }

        // 计算旋转角度
        float lenA = Math.sqrt(AB[0] * AB[0] + AB[1] * AB[1] + AB[2] * AB[2]);
        float lenB = Math.sqrt(CD[0] * CD[0] + CD[1] * CD[1] + CD[2] * CD[2]);

        // 防止数值不稳定
        float cosAngle = dotProduct / (lenA * lenB);
        cosAngle = Math.max(-1.0f, Math.min(1.0f, cosAngle));

        // 创建四元数
        fromAxisAngle(result, axis, Math.acos(cosAngle));
    }
}
