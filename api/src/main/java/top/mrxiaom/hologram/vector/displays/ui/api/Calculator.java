package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import top.mrxiaom.hologram.vector.displays.ui.widget.Triangle;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

/**
 * 元素内部位置计算器实例
 */
@ApiStatus.Internal
public class Calculator {
    private final Element<?, ?> element;
    private boolean planeReady = false;
    private double[] p1, p2, p3, vX, vY;
    protected Calculator(Element<?, ?> element) {
        this.element = element;
    }

    public void refreshPlane() {
        if (element.terminal() == null) return;
        if (element instanceof Triangle triangle) {
            // 三角形使用重心作为原点坐标
            float[] pos1 = triangle.getPos1();
            float[] pos2 = triangle.getPos2();
            float[] pos3 = triangle.getPos3();
            // 正常运行到这里时，这三个坐标必不可能是 null
            assert pos1 != null && pos2 != null && pos3 != null;
            // 获取三角形在平面上的二维重心
            double centerX = (pos1[0] + pos2[0] + pos3[0]) / 3.0;
            double centerY = (pos1[1] + pos2[1] + pos3[1]) / 3.0;
            // 三个用于确定平面的三维坐标，确保不共线
            double[] center = decideLocation(centerX, centerY, true); // 原点坐标
            double[] p1 = decideLocation(pos1[0], pos1[1], true);
            double[] p3 = decideLocation(pos3[0], pos3[1], true);
            double[] pY = decideLocation(centerX, centerY - 100, true, true); // y轴向量末端坐标
            double[] pX = decideLocation(centerX + 100, centerY, true, true); // x轴向量末端坐标
            this.p1 = center;
            this.p2 = p1;
            this.p3 = p3;
            this.vX = toVector(center, pX);
            this.vY = toVector(center, pY);
            this.planeReady = true;
        } else {
            // 其它元素使用元素坐标作为原点坐标
            double x = element.getX();
            double y = element.getY();
            // 三个用于确定平面的三维坐标，确保不共线
            double[] p1 = decideLocation(x, y, true); // 原点坐标
            double[] p2 = decideLocation(x - 100, y + 100, true);
            double[] p3 = decideLocation(x + 100, y + 100, true);
            double[] pY = decideLocation(x, y - 100, true, true); // y轴向量末端坐标
            double[] pX = decideLocation(x + 100, y, true, true); // x轴向量末端坐标
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.vX = toVector(p1, pX);
            this.vY = toVector(p1, pY);
            this.planeReady = true;
        }
    }

    private static double[] toVector(double[] start, double[] end) {
        return new double[] { end[0] - start[0], end[1] - start[1], end[2] - start[2] };
    }

    public double[] projectToPlane(double[] pA) {
        if (!planeReady) {
            refreshPlane();
        }
        return HologramUtils.projectToPlane(p1, p2, p3, vX, vY, pA);
    }

    public double[] decideLocation(double pX, double pY, boolean rotate) {
        return decideLocation(pX, pY, rotate, false);
    }
    public double[] decideLocation(double pX, double pY, boolean rotate, boolean additionalRotate) {
        double[] raw = element.decideLocationRaw(pX, pY);
        Terminal<?> terminal = element.getTerminal();
        if (rotate) {
            // 应用额外旋转
            if (additionalRotate) {
                float[] ar = element.getAdditionalRotation();
                if (ar != null) {
                    double[] origin = decideLocation(element.getX(), element.getY(), true);
                    raw = QuaternionUtils.rotateChildrenToDouble(origin, ar, raw);
                }
            }
            // TODO: 考虑 parent
            Location origin = terminal.getLocation();
            float[] rotation = terminal.getRotation();
            raw = QuaternionUtils.rotateChildrenToDouble(origin, rotation, raw);
        }
        double[] transform = element.isFixedLocation()
                ? element.decideTranslation()
                : element.getAdditionalTranslation();
        HologramUtils.add(raw, transform);
        return raw;
    }
}
