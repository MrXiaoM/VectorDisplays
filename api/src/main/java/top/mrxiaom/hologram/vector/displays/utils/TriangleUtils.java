package top.mrxiaom.hologram.vector.displays.utils;

import net.kyori.adventure.text.Component;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.utils.Vector3F;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;
import top.mrxiaom.hologram.vector.displays.utils.joml.Matrix4f;
import top.mrxiaom.hologram.vector.displays.utils.joml.Quaternionf;
import top.mrxiaom.hologram.vector.displays.utils.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static top.mrxiaom.hologram.vector.displays.utils.joml.Math.toRadians;

/**
 * <a href="https://github.com/TheCymaera/minecraft-hologram/blob/main/src/main/java/com/heledron/hologram/utilities/rendering/textDisplays.kt">TheCymaera/minecraft-hologram</a>
 */
public class TriangleUtils {
    public static Matrix4f textDisplayUnitSquare() {
        return new Matrix4f(NMS.textDisplayUnitSquare());
    }
    /**
     * 构造局部顶点为 (0, 0)、(1, 0)、(0, 1) 的单位三角形的三个背景片。
     * 此处为 y 向上的局部坐标，三个片的可见面均朝局部 +Z。
     */
    public static List<Matrix4f> textDisplayUnitTriangle() {
        Matrix4f square = textDisplayUnitSquare();
        return new ArrayList<>(List.of(
                // Left
                new Matrix4f().scale(.5f).mul(square),
                // Right
                shear(new Matrix4f().scale(.5f).translate(1f, 0f, 0f), 0, 0, -1f, 0, 0, 0).mul(square),
                // Top
                shear(new Matrix4f().scale(.5f).translate(0f, 1f, 0f), -1f, 0, 0, 0, 0, 0).mul(square)
        ));
    }
    public static class Result {
        public final Vector3f point1, point2, point3;
        public final Vector3f xAxis, yAxis, zAxis;
        public final float height;
        public final float width;
        public final Quaternionf rotation;
        public final float shear;

        public Result(Vector3f point1, Vector3f point2, Vector3f point3,
                      Vector3f xAxis, Vector3f yAxis, Vector3f zAxis,
                      float height, float width, Quaternionf rotation, float shear) {
            this.point1 = point1;
            this.point2 = point2;
            this.point3 = point3;
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.zAxis = zAxis;
            this.height = height;
            this.width = width;
            this.rotation = rotation;
            this.shear = shear;
        }

        /**
         * <a href="https://github.com/TheCymaera/minecraft-hologram/blob/d67eb43308df61bdfe7283c6821312cca5f9dea9/src/main/java/com/heledron/hologram/triangle_visualizer/setupTriangleVisualizer.kt#L150-L219">TheCymaera/minecraft-hologram</a>
         * @param creator 实体创建器，输入索引，返回一个实体
         * @param origin 基于哪个坐标作为原点进行变换，这个坐标是文本展示实体的实体坐标
         */
        public List<EntityTextDisplay> create(
                Function<Integer, EntityTextDisplay> creator,
                Vector3f origin
        ) {
            Vector3f p1 = new Vector3f(point1).sub(origin);
            List<EntityTextDisplay> list = new ArrayList<>();
            List<Matrix4f> transforms = new ArrayList<>();

            for (Matrix4f unit : textDisplayUnitTriangle()) {
                Matrix4f out = new Matrix4f();

                out.translate(p1);
                out.rotate(rotation);

                out.scale(width, height, 1f);
                transforms.add(shear(out, 0, 0, shear, 0, 0, 0).translate(0f, 0f, .01f).mul(unit));
            }

            for (int i = 0; i < transforms.size(); i++) {
                Matrix4f piece = transforms.get(i);
                EntityTextDisplay entity = creator.apply(i);

                entity.setText(Component.space());
                interpolateTriangleTransform(entity, piece);

                list.add(entity);
            }
            return list;
        }
    }
    /**
     * 根据三个世界坐标顶点计算单面三角形的变换。
     * <p>
     * 可见面的法线为 (point2 - point1) × (point3 - point1)，按右手法则确定。
     * 从预期可见的一侧观察时，point1、point2、point3 应为逆时针顺序；
     * 交换任意两个顶点会翻转可见面，客户端会剔除背面。
     * <p>
     * 与三角形控件的 UI 坐标不同，本方法接收的是已完成坐标转换的三维顶点。
     * UI 的 y 轴向下，因此 UI 二倍有向面积小于 0 的逆时针序列，
     * 转为 y 向上的平面坐标后，法线才朝向终端正面。
     * @param point1 第一个世界坐标顶点
     * @param point2 第二个世界坐标顶点
     * @param point3 第三个世界坐标顶点；三个点必须互不重合且不共线
     * @return 保留输入顶点顺序所确定的朝向，不自动调整正反面
     */
    public static Result textDisplayTriangle(
            Vector3f point1,
            Vector3f point2,
            Vector3f point3
    ) {
        Vector3f p2 = new Vector3f(point2).sub(point1);
        Vector3f p3 = new Vector3f(point3).sub(point1);

        // 叉乘顺序决定三个背景片的正面朝向，不能在不改变可见面的情况下交换顶点。
        Vector3f zAxis = new Vector3f(p2).cross(p3).normalize();
        Vector3f xAxis = new Vector3f(p2).normalize();
        Vector3f yAxis = new Vector3f(zAxis).cross(xAxis).normalize();

        float width = p2.length();
        float height = new Vector3f(p3).dot(yAxis);
        float p3Width = new Vector3f(p3).dot(xAxis);

        Quaternionf rotation = new Quaternionf().lookAlong(new Vector3f(zAxis).mul(-1f), yAxis).conjugate();

        float shear = p3Width / width;

        return new Result(
                point1, point2, point3,
                xAxis, yAxis, zAxis,
                height, width,
                rotation, shear
        );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static void interpolateTriangleTransform(EntityTextDisplay entity, Matrix4f matrix) {
        Matrix4f oldTransformation = entity.getTransformationMatrixFixed();
        float[] oldRight = entity.getRightRotation();
        entity.setTransformationMatrix(matrix);

        float[] newRight = entity.getRightRotation();

        Vector3f rightRotationChange = new Quaternionf(oldRight[0], oldRight[1], oldRight[2], oldRight[3])
                .difference(new Quaternionf(newRight[0], newRight[1], newRight[2], newRight[3]))
                .getEulerAnglesXYZ(new Vector3f());

        if (Math.abs(rightRotationChange.z) >= toRadians(45f)) {
            float rot = toRadians(-90f) * Math.signum(rightRotationChange.z);

            float[] left = entity.getLeftRotation();
            Quaternionf leftRotation = new Quaternionf(left[0], left[1], left[2], left[3]);
            leftRotation.rotateZ(-rot);
            entity.setLeftRotation(leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w);

            Vector3F scale = entity.getScale();
            entity.setScale(scale.y, scale.x, scale.z);

            float[] right = entity.getRightRotation();
            Quaternionf rightRotation = new Quaternionf(right[0], right[1], right[2], right[3]);
            rightRotation.rotateZ(rot);
            entity.setRightRotation(rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w);
        }

        if (oldTransformation.equals(entity.getTransformationMatrixFixed())) return;
        entity.setInterpolationDurationTransformation(0);
    }

    private static Matrix4f shearMatrix(
            float xy, float xz,
            float yx, float yz,
            float zx, float zy
    ) {
        return new Matrix4f(
                1f, xy, xz, 0f,
                yx, 1f, yz, 0f,
                zx, zy, 1f, 0f,
                0f, 0f, 0f, 1f
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static Matrix4f shear(
            Matrix4f matrix,
            float xy, float xz,
            float yx, float yz,
            float zx, float zy
    ) {
        return matrix.mul(shearMatrix(
                xy, xz,
                yx, yz,
                zx, zy
        ));
    }

}
