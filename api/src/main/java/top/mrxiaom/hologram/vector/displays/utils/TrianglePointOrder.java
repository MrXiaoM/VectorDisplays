package top.mrxiaom.hologram.vector.displays.utils;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.ui.widget.Triangle;

import java.util.Objects;

/**
 * 三角形 UI 顶点的绕序工具，坐标约定为 x 向右、y 向下。
 * <p>
 * 逆时针指从终端正面观察的顺序，不适用于未经投影的三维世界坐标。
 * 排序保留第一个顶点，仅在需要时交换第二、第三个顶点，不修改输入数组。
 */
public final class TrianglePointOrder {
    private TrianglePointOrder() {
    }

    /**
     * 将三个 UI 顶点按从终端正面观察的逆时针顺序排列。
     *
     * @param point1 第一个顶点，包含 x、y 两个坐标
     * @param point2 第二个顶点，包含 x、y 两个坐标
     * @param point3 第三个顶点，包含 x、y 两个坐标
     * @return 三个顶点的独立副本，第一个顶点保持不变
     * @throws IllegalArgumentException 坐标数组不含恰好两个有限数值，或三个点重合、共线
     */
    public static float[][] sortCounterClockwise(
            float @NotNull [] point1,
            float @NotNull [] point2,
            float @NotNull [] point3
    ) {
        validate(point1, "point1");
        validate(point2, "point2");
        validate(point3, "point3");

        // 先提升到 double 再相减，避免有限 float 坐标在差值或叉乘时溢出。
        double dx2 = (double) point2[0] - point1[0];
        double dy2 = (double) point2[1] - point1[1];
        double dx3 = (double) point3[0] - point1[0];
        double dy3 = (double) point3[1] - point1[1];
        double area = dx2 * dy3 - dy2 * dx3;
        if (area == 0.0) {
            throw new IllegalArgumentException("三个顶点必须互不重合且不共线");
        }

        // UI 的 y 轴向下，二倍有向面积小于 0 才是正面观察时的逆时针。
        return area < 0.0
                ? new float[][] { point1.clone(), point2.clone(), point3.clone() }
                : new float[][] { point1.clone(), point3.clone(), point2.clone() };
    }

    /**
     * 排序后将三个 UI 顶点一次应用到三角形，并返回该控件以便链式调用。
     * <p>
     * 本方法只设置顶点，不自动提交位置或渲染更新；已生成的控件仍需调用
     * {@link Triangle#updateLocation()} 和 {@link Triangle#update()}。
     *
     * @param triangle 目标三角形
     * @param point1 第一个 UI 顶点
     * @param point2 第二个 UI 顶点
     * @param point3 第三个 UI 顶点
     * @return 传入的三角形控件
     * @throws IllegalArgumentException 坐标无效，或三个点重合、共线；此时不修改控件
     */
    public static @NotNull Triangle applyCounterClockwise(
            @NotNull Triangle triangle,
            float @NotNull [] point1,
            float @NotNull [] point2,
            float @NotNull [] point3
    ) {
        Objects.requireNonNull(triangle, "triangle");
        float[][] points = sortCounterClockwise(point1, point2, point3);
        return triangle.setPos1(points[0]).setPos2(points[1]).setPos3(points[2]);
    }

    private static void validate(float[] point, String name) {
        if (point == null || point.length != 2
                || !Float.isFinite(point[0]) || !Float.isFinite(point[1])) {
            throw new IllegalArgumentException(name + " 必须包含 x、y 坐标");
        }
    }
}
