package top.mrxiaom.hologram.vector.displays.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.api.IEyeLocationAdapter;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.*;

import java.math.BigDecimal;
import java.util.List;

public class HologramUtils {
    private static IEyeLocationAdapter eyeLocationAdapter = IEyeLocationAdapter.DEFAULT;
    public static double LINE_HEIGHT = 12.5;
    private static final double EPSILON = 1e-10; // 用于处理浮点数精度问题

    public static void setEyeLocationAdapter(@NotNull IEyeLocationAdapter eyeLocationAdapter) {
        HologramUtils.eyeLocationAdapter = eyeLocationAdapter;
    }

    @NotNull
    public static Location getEyeLocation(@NotNull Player player) {
        return eyeLocationAdapter.getEyeLocation(player);
    }

    public static boolean isLeftClick(Action action) {
        return action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK);
    }

    public static boolean isRightClick(Action action) {
        return action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
    }

    /**
     * 尝试执行点击元素操作
     * @param player 玩家
     * @param action 点击操作
     * @param eyeLocation <code>HologramUtils.getEyeLocation(player)</code>
     * @param elements 待处理点击操作的元素列表
     * @param rotation 父面板旋转变换
     * @param interactDistance 可交互距离 (单位: 方块)
     * @return <code>true</code> 代表已有元素处理点击事件
     */
    public static boolean commonPerformClick(Player player, Action action, Location eyeLocation, List<Element<?, ?>> elements, float[] rotation, double interactDistance) {
        for (Element<?, ?> element : elements) {
            if (element.isEnabled()) {
                if (element.beforePerformClick(player, action, eyeLocation)) {
                    return true;
                }
                if (element.commonPerformClick(player, action, eyeLocation, rotation, interactDistance)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float[] toFloat(double... doubles) {
        float[] floats = new float[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            floats[i] = Float.parseFloat(String.valueOf(doubles[i]));
        }
        return floats;
    }

    /**
     * 根据旧尺寸和新尺寸，计算缩放
     * @param oldLength 旧尺寸
     * @param newLength 新尺寸
     * @return 缩放大小
     */
    public static float calculateScale(double oldLength, double newLength) {
        float vNew = BigDecimal.valueOf(newLength).floatValue();
        float vOld = BigDecimal.valueOf(oldLength).floatValue();
        return vNew / vOld;
    }

    /**
     * @see HologramUtils#raytraceElement(float[], float[], Location, double, double, double, double, Location)
     */
    @Nullable
    public static Location raytraceElement(@NotNull Terminal<?> terminal, float @Nullable [] additionalRotation, @NotNull Element<?, ?> element, @NotNull Location eyeLocation) {
        return raytraceElement(terminal.getRotation(), additionalRotation, element, eyeLocation);
    }

    /**
     * @see HologramUtils#raytraceElement(float[], float[], Location, double, double, double, double, Location)
     */
    @Nullable
    public static Location raytraceElement(float @NotNull [] rotation, float @Nullable [] additionalRotation, @NotNull Element<?, ?> element, @NotNull Location eyeLocation) {
        // 获取悬浮字宽高
        double width = element.getWidth();
        double height = element.getHeight();
        // 根据悬浮字类型，计算偏移值
        double offsetX, offsetY;
        if (element instanceof ItemElement<?>) {
            offsetX = 0.0;
            offsetY = -height / 2.0;
        } else {
            offsetX = offsetY = 0.0;
        }
        // 悬浮字正下方坐标
        Location loc = element.getEntity().getLocation();
        return raytraceElement(rotation, additionalRotation, loc, offsetX, offsetY, width, height, eyeLocation);
    }

    /**
     * @see HologramUtils#raytraceElement(float[], float[], Location, double, double, double, double, Location)
     */
    @Nullable
    public static Location raytraceElement(float @NotNull [] rotation, float @Nullable [] additionalRotation, @Nullable Location loc, double width, double height, @NotNull Location eyeLocation) {
        return raytraceElement(rotation, additionalRotation, loc, 0, 0, width, height, eyeLocation);
    }
    /**
     * 获取玩家的视线落在了元素的实体上的世界坐标
     *
     * @param rotation 终端面板旋转量
     * @param additionalRotation 悬浮字的额外旋转量
     * @param loc 待判定的悬浮字的正下方坐标
     * @param offsetX 元素X位置偏移值
     * @param offsetY 元素Y位置偏移值
     * @param width 悬浮字宽度
     * @param height 悬浮字高度
     * @param eyeLocation 玩家视线位置，<code>HologramUtils.getEyeLocation(player)</code>
     * @return 如果视线没有落在悬浮字上，返回 <code>null</code>
     */
    @Nullable
    public static Location raytraceElement(float @NotNull [] rotation, float @Nullable [] additionalRotation, @Nullable Location loc, double offsetX, double offsetY, double width, double height, @NotNull Location eyeLocation) {
        if (loc == null) return null;
        // 悬浮字四角顶点
        double paddingHorizontal = 0.02;
        double paddingVertical = 0.01;
        Location loc1 = loc.clone(); // 左上角
        loc1.setX(loc1.getX() + offsetX - (width / 2.0) - paddingHorizontal);
        loc1.setY(loc1.getY() + offsetY + height + paddingVertical);
        Location loc2 = loc.clone(); // 右上角
        loc2.setX(loc2.getX() + offsetX + (width / 2.0) + paddingHorizontal);
        loc2.setY(loc2.getY() + offsetY + height + paddingVertical);
        Location loc3 = loc.clone(); // 左下角
        loc3.setX(loc3.getX() + offsetX - (width / 2.0) - paddingHorizontal);
        loc3.setY(loc3.getY() + offsetY - paddingVertical);
        Location loc4 = loc.clone(); // 右下角
        loc4.setX(loc4.getX() + offsetX + (width / 2.0) + paddingHorizontal);
        loc4.setY(loc3.getY() + offsetY - paddingVertical);
        // 获取终端面板的旋转四元数，进行变换后再输入计算交点
        float[] r;
        if (additionalRotation == null) {
            r = rotation;
        } else {
            // 如果有额外旋转，先进行额外旋转（悬浮字本地旋转），再进行父组件的旋转
            r = QuaternionUtils.multiplyF(rotation, additionalRotation);
        }
        // 计算交点
        return calculateIntersection(loc, r, loc1, loc2, loc3, loc4, eyeLocation);
    }
    /**
     * 过时方法
     * @see HologramUtils#raytraceElement(float[], float[], Location, double, double, double, double, Location)
     */
    @Nullable
    @Deprecated
    public static Location raytraceHologram(@NotNull Terminal<?> terminal, float @Nullable [] additionalRotation, @NotNull EntityTextDisplay hologram, @NotNull Location eyeLocation) {
        float[] rotation = terminal.getRotation();
        // 计算悬浮字宽高
        Component text = hologram.getTextAsComponent();
        double width = HologramFont.getWidth(text) * HologramFont.getCharScale() * hologram.getScale().x;
        double height = HologramFont.getLines(text) * LINE_HEIGHT * HologramFont.getCharScale() * hologram.getScale().y;
        // 悬浮字正下方坐标
        Location loc = hologram.getLocation();
        return raytraceElement(rotation, additionalRotation, loc, width, height, eyeLocation);
    }

    /**
     * 过时方法
     * @see HologramUtils#raytraceElement(float[], float[], Location, double, double, double, double, Location)
     */
    @Nullable
    @Deprecated
    public static Location raytraceHologram(@NotNull Terminal<?> terminal, float @Nullable [] additionalRotation, @NotNull EntityItemDisplay hologram, @NotNull Location eyeLocation) {
        float[] rotation = terminal.getRotation();
        // 计算悬浮字宽高
        double width = ItemElement.scaleWidth * HologramFont.getCharScale() * hologram.getScale().x;
        double height = ItemElement.scaleHeight * HologramFont.getCharScale() * hologram.getScale().y;
        // 悬浮字正下方坐标
        Location loc = hologram.getLocation();
        return raytraceElement(rotation, additionalRotation, loc, width, height, eyeLocation);
    }

    /**
     * 计算射线与平面上矩形的交点，要求射线与平面特定方向相同
     * @param loc 悬浮字旋转中心坐标
     * @param r 终端面板旋转量四元数
     * @param loc1 矩形左上角坐标
     * @param loc2 矩形右上角坐标
     * @param loc3 矩形左下角坐标
     * @param loc4 矩形右下角坐标
     * @param eyeLocation 玩家视线位置，<code>HologramUtils.getEyeLocation(player)</code>
     */
    public static Location calculateIntersection(
            Location loc, float[] r,
            Location loc1, Location loc2,
            Location loc3, Location loc4,
            Location eyeLocation
    ) {
        // 计算交点
        Location point = calculateIntersection(
                eyeLocation,
                QuaternionUtils.rotateChildren(loc, r, loc1),
                QuaternionUtils.rotateChildren(loc, r, loc2),
                QuaternionUtils.rotateChildren(loc, r, loc3),
                QuaternionUtils.rotateChildren(loc, r, loc4));
        if (point == null) return null;
        // 最后进行校验，要求 射线方向 和 终端面板方向 的夹角大于 90 度，才能算作成功
        // 即玩家必须要面向终端面板的正面，才能进行悬停和点击操作
        double[] vectorA = toDirectionVector(r);
        double[] vectorB = toVector(eyeLocation, point);
        double angle = Math.abs(calculateAngle(vectorA, vectorB));
        return angle >= 90 ? point : null;
    }

    /**
     * 计算向量 AB
     * @param locA 点A
     * @param locB 点B
     */
    public static double[] toVector(Location locA, Location locB) {
        return new double[] { locB.getX() - locA.getX(), locB.getY() - locA.getY(), locB.getZ() - locA.getZ() };
    }

    /**
     * 将四元数转换为向量
     * @param quaternion 四元数
     */
    public static double[] toDirectionVector(float[] quaternion) {
        // 提取四元数分量并进行计算
        float xx = quaternion[0] * quaternion[0], yy = quaternion[1] * quaternion[1], zz = quaternion[2] * quaternion[2], ww = quaternion[3] * quaternion[3];
        float xy = quaternion[0] * quaternion[1], xz = quaternion[0] * quaternion[2], yz = quaternion[1] * quaternion[2], xw = quaternion[0] * quaternion[3];
        float zw = quaternion[2] * quaternion[3], yw = quaternion[1] * quaternion[3], k = 1 / (xx + yy + zz + ww);
        // 将悬浮字的默认方向 (0, 0, 1) 旋转到当前方向
        double vX = 0, vY = 0, vZ = 1;
        return new double[] {
                org.joml.Math.fma((xx - yy - zz + ww) * k, vX, org.joml.Math.fma(2 * (xy - zw) * k, vY, (2 * (xz + yw) * k) * vZ)),
                org.joml.Math.fma(2 * (xy + zw) * k, vX, org.joml.Math.fma((yy - xx - zz + ww) * k, vY, (2 * (yz - xw) * k) * vZ)),
                org.joml.Math.fma(2 * (xz - yw) * k, vX, org.joml.Math.fma(2 * (yz + xw) * k, vY, ((zz - xx - yy + ww) * k) * vZ))
        };
    }

    /**
     * 计算射线与平面上矩形的交点 (豆包AI 生成)
     *
     * @param eyeLocation 玩家视角位置
     * @param p1 矩形顶点1
     * @param p2 矩形顶点2
     * @param p3 矩形顶点3
     * @param p4 矩形顶点4
     * @return 交点坐标，若视线射线与平面平行，或交点不在矩形上，则返回 null
     */
    public static Location calculateIntersection(Location eyeLocation, Location p1, Location p2, Location p3, Location p4) {
        Vector direction = eyeLocation.getDirection();
        double aX = eyeLocation.getX();
        double aY = eyeLocation.getY();
        double aZ = eyeLocation.getZ();
        double vX = direction.getX();
        double vY = direction.getY();
        double vZ = direction.getZ();
        double p1X = p1.getX();
        double p1Y = p1.getY();
        double p1Z = p1.getZ();
        double p2X = p2.getX();
        double p2Y = p2.getY();
        double p2Z = p2.getZ();
        double p3X = p3.getX();
        double p3Y = p3.getY();
        double p3Z = p3.getZ();
        double p4X = p4.getX();
        double p4Y = p4.getY();
        double p4Z = p4.getZ();
        // 第一步：计算射线与平面的交点（使用前三个点确定平面）
        double[] q = calculateRayPlaneIntersection(
                aX, aY, aZ, vX, vY, vZ,
                p1X, p1Y, p1Z, p2X, p2Y, p2Z, p3X, p3Y, p3Z
        );

        // 如果射线与平面不相交，直接返回null
        if (q == null) {
            return null;
        }

        // 将点转换为数组形式便于处理
        double[] p1array = {p1X, p1Y, p1Z};
        double[] p2array = {p2X, p2Y, p2Z};
        double[] p3array = {p3X, p3Y, p3Z};
        double[] p4array = {p4X, p4Y, p4Z};

        // 第二步：确定矩形的顶点O和相邻边向量u、v
        double[] O = p1array;
        double[] u;
        double[] v;

        // 从其他三个点中找到与p1构成矩形相邻边的两个点
        double[] vec2 = subtract(p2array, O);
        double[] vec3 = subtract(p3array, O);
        double[] vec4 = subtract(p4array, O);

        // 计算各向量的点积，找到垂直的向量对（矩形相邻边互相垂直）
        if (Math.abs(dotProduct(vec2, vec3)) < EPSILON) {
            u = vec2;
            v = vec3;
        } else if (Math.abs(dotProduct(vec2, vec4)) < EPSILON) {
            u = vec2;
            v = vec4;
        } else if (Math.abs(dotProduct(vec3, vec4)) < EPSILON) {
            u = vec3;
            v = vec4;
        } else {
            // 如果p1不是合适的原点，尝试p2作为原点
            O = p2array;
            vec2 = subtract(p1array, O);
            vec3 = subtract(p3array, O);
            vec4 = subtract(p4array, O);

            if (Math.abs(dotProduct(vec2, vec3)) < EPSILON) {
                u = vec2;
                v = vec3;
            } else if (Math.abs(dotProduct(vec2, vec4)) < EPSILON) {
                u = vec2;
                v = vec4;
            } else if (Math.abs(dotProduct(vec3, vec4)) < EPSILON) {
                u = vec3;
                v = vec4;
            } else {
                return null;
            }
        }

        // 第三步：判断交点是否在矩形内部
        double[] w = subtract(q, O); // 从O到交点的向量

        // 计算u和v的模长平方（避免除以零）
        double uDotU = dotProduct(u, u);
        double vDotV = dotProduct(v, v);
        if (uDotU < EPSILON || vDotV < EPSILON) {
            return null;
        }

        // 计算参数s和t（将w分解到u和v方向上）
        double s = dotProduct(w, u) / uDotU;
        double t = dotProduct(w, v) / vDotV;

        // 检查s和t是否在[0, 1]范围内（考虑浮点误差）
        if (s >= -EPSILON && s <= 1 + EPSILON &&
                t >= -EPSILON && t <= 1 + EPSILON) {
            return new Location(eyeLocation.getWorld(), q[0], q[1], q[2]);
        } else {
            return null; // 交点在矩形外
        }
    }

    /**
     * 计算射线与平面的交点
     */
    private static double[] calculateRayPlaneIntersection(
            double aX, double aY, double aZ,
            double vX, double vY, double vZ,
            double p1X, double p1Y, double p1Z,
            double p2X, double p2Y, double p2Z,
            double p3X, double p3Y, double p3Z) {

        // 计算平面的两个向量
        double v1X = p2X - p1X;
        double v1Y = p2Y - p1Y;
        double v1Z = p2Z - p1Z;

        double v2X = p3X - p1X;
        double v2Y = p3Y - p1Y;
        double v2Z = p3Z - p1Z;

        // 计算平面的法向量（v1 × v2）
        double normalX = v1Y * v2Z - v1Z * v2Y;
        double normalY = v1Z * v2X - v1X * v2Z;
        double normalZ = v1X * v2Y - v1Y * v2X;

        // 计算射线方向向量与平面法向量的点积
        double denominator = normalX * vX + normalY * vY + normalZ * vZ;

        // 如果点积接近0，射线与平面平行或在平面上
        if (Math.abs(denominator) < EPSILON) {
            return null;
        }

        // 计算平面方程的d值：normal · p1 + d = 0 => d = -normal · p1
        double d = -(normalX * p1X + normalY * p1Y + normalZ * p1Z);

        // 计算参数t
        double numerator = -(normalX * aX + normalY * aY + normalZ * aZ + d);
        double t = numerator / denominator;

        // 射线的参数t必须大于等于0（在射线方向上）
        if (t < -EPSILON) {
            return null;
        }

        // 计算交点坐标
        double x = aX + t * vX;
        double y = aY + t * vY;
        double z = aZ + t * vZ;

        return new double[]{x, y, z};
    }
    /**
     * 向量相减：a - b
     */
    private static double[] subtract(double[] a, double[] b) {
        return new double[]{
                a[0] - b[0],
                a[1] - b[1],
                a[2] - b[2]
        };
    }
    /**
     * 向量点积
     */
    private static double dotProduct(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }
    /**
     * 计算三维向量的模长
     */
    private static double magnitude(double[] vector) {
        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    }
    /**
     * 计算两个三维向量之间的夹角（角度制）
     *
     * @param vectorA 第一个向量，格式为 double[] {x, y, z}
     * @param vectorB 第二个向量，格式为 double[] {x, y, z}
     * @return 两个向量之间的夹角（角度制，范围0-180）
     * @throws IllegalArgumentException 如果输入向量为null或长度不为3
     */
    public static double calculateAngle(double[] vectorA, double[] vectorB) {
        // 计算点积
        double dotProduct = dotProduct(vectorA, vectorB);

        // 计算两个向量的模长
        double magnitudeA = magnitude(vectorA);
        double magnitudeB = magnitude(vectorB);

        // 防止除以零
        if (magnitudeA == 0 || magnitudeB == 0) {
            throw new ArithmeticException("向量的模长不能为零");
        }

        // 计算cosθ
        double cosTheta = dotProduct / (magnitudeA * magnitudeB);

        // 处理由于浮点计算误差导致的略微超出范围的情况
        cosTheta = Math.max(Math.min(cosTheta, 1.0), -1.0);

        // 计算弧度并转换为角度
        double angleRadians = Math.acos(cosTheta);
        return Math.toDegrees(angleRadians);
    }

    /**
     * 在元素坐标原点处建立平面直角坐标系，计算世界位置在元素上的投影坐标
     * @param element 元素实例
     * @param loc 世界位置
     * @return 投影坐标 (使用文本坐标系)
     */
    public static Point2D getPoint(Element<?, ?> element, Location loc) {
        Calculator calc = element.calc();
        double x = element.getX();
        double y = element.getY();
        double[] p1 = calc.decideLocation(x, y, true);
        double[] p2 = calc.decideLocation(x - 100, y + 100, true);
        double[] p3 = calc.decideLocation(x + 100, y + 100, true);
        double[] p4 = calc.decideLocation(x, y - 100, true, true);
        double[] p5 = calc.decideLocation(x + 100, y, true, true);
        // 计算投影
        double[] result = projectToPlane(p1, p2, p3, toVector(p1, p5), toVector(p1, p4), toArray(loc));
        // 将结果转换为文本坐标系
        double charScale = HologramFont.getCharScale();
        return new Point2D(result[0] / charScale, result[1] / charScale);
    }

    private static double[] toArray(Location loc) {
        return new double[] { loc.getX(), loc.getY(), loc.getZ() };
    }

    private static double[] toVector(double[] start, double[] end) {
        return new double[] { end[0] - start[0], end[1] - start[1], end[2] - start[2] };
    }

    /**
     * 将三维空间中的点pA投影到由p1, p2, p3定义的平面坐标系中
     *
     * @param p1 平面上的点1，作为新坐标系的原点
     * @param p2 平面上的点2
     * @param p3 平面上的点3
     * @param vX x轴方向向量
     * @param vY y轴方向向量
     * @param pA 待投影的三维空间点
     * @return double[2] 投影后在平面坐标系中的坐标 [x, y]
     */
    public static double[] projectToPlane(double[] p1, double[] p2, double[] p3,
                                          double[] vX, double[] vY, double[] pA) {
        // 步骤1: 计算平面的两个方向向量
        double[] v1 = subtract(p2, p1);  // p1到p2的向量
        double[] v2 = subtract(p3, p1);  // p1到p3的向量

        // 步骤2: 计算平面的法向量 (v1 × v2)
        double[] normal = crossProduct(v1, v2);
        normal = normalize(normal);

        // 步骤3: 将pA投影到平面上
        // 投影公式: pA_projected = pA - ((pA - p1) · normal) * normal
        double[] pA_minus_p1 = subtract(pA, p1);
        double distance = dotProduct(pA_minus_p1, normal);
        double[] projection = subtract(pA, scalarMultiply(normal, distance));

        // 步骤4: 将vY投影到平面上，作为y轴方向
        // vY_projected = vY - (vY · normal) * normal
        double vY_dot_normal = dotProduct(vY, normal);
        double[] vY_projected = subtract(vY, scalarMultiply(normal, vY_dot_normal));
        vY_projected = normalize(vY_projected);

        // 步骤5: 处理vX向量，计算x轴方向
        // 5.1 将vX投影到平面上（确保x轴在平面内）
        double vX_dot_normal = dotProduct(vX, normal);
        double[] vX_projected = subtract(vX, scalarMultiply(normal, vX_dot_normal));
        vX_projected = normalize(vX_projected);

        // 5.2 正交化处理（确保x轴与y轴垂直）
        // 公式：x轴 = vX投影 - (vX投影 · y轴) * y轴（格拉姆-施密特正交化）
        double dotProductXV = dotProduct(vX_projected, vY_projected);
        double[] xAxis = subtract(vX_projected, scalarMultiply(vY_projected, dotProductXV));
        xAxis = normalize(xAxis);  // 归一化得到最终x轴

        // 步骤6: 计算投影点相对于p1的向量
        double[] relativePos = subtract(projection, p1);

        // 步骤7: 计算在新坐标系中的坐标
        double x = dotProduct(relativePos, xAxis);
        double y = dotProduct(relativePos, vY_projected);

        return new double[]{x, y};
    }

    /**
     * 标量乘法: scalar * v
     */
    private static double[] scalarMultiply(double[] v, double scalar) {
        return new double[]{v[0] * scalar, v[1] * scalar, v[2] * scalar};
    }

    /**
     * 向量叉积: a × b
     */
    private static double[] crossProduct(double[] a, double[] b) {
        return new double[]{
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    /**
     * 向量归一化
     */
    private static double[] normalize(double[] v) {
        double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (length < 1e-10) {
            throw new IllegalArgumentException("Cannot normalize zero vector");
        }
        return new double[]{v[0] / length, v[1] / length, v[2] / length};
    }

}
