package top.mrxiaom.hologram.vector.displays.utils;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.utils.Vector3F;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * <a href="https://github.com/TheCymaera/minecraft-hologram/blob/main/src/main/java/com/heledron/hologram/utilities/rendering/textDisplays.kt">TheCymaera/minecraft-hologram</a>
 */
public class TriangleUtils {
    public static Matrix4f textDisplayUnitSquare() {
        return NMS.textDisplayUnitSquare();
    }
    public static List<Matrix4f> textDisplayUnitTriangle() {
        return Lists.newArrayList(
                // Left
                new Matrix4f().scale(.5f).mul(textDisplayUnitSquare()),
                // Right
                shear(new Matrix4f().scale(.5f).translate(1f, 0f, 0f), 0, 0, -1f, 0, 0, 0).mul(textDisplayUnitSquare()),
                // Top
                shear(new Matrix4f().scale(.5f).translate(0f, 1f, 0f), -1f, 0, 0, 0, 0, 0).mul(textDisplayUnitSquare())
        );
    }
    public static class Result {
        public final Vector3f point1, point2, point3;
        public final Vector3f xAxis, yAxis, zAxis;
        public final float height;
        public final float width;
        public final Quaternionf rotation;
        public final float shear;

        public Result(Vector3f point1, Vector3f point2, Vector3f point3, Vector3f xAxis, Vector3f yAxis, Vector3f zAxis, float height, float width, Quaternionf rotation, float shear) {
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
    public static Result textDisplayTriangle(
            Vector3f point1,
            Vector3f point2,
            Vector3f point3
    ) {
        Vector3f p2 = new Vector3f(point2).sub(point1);
        Vector3f p3 = new Vector3f(point3).sub(point1);

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
        Matrix4f oldTransformation = new Matrix4f(entity.getTransformationMatrix());
        float[] oldRight = entity.getRightRotation();
        entity.setTransformationMatrix(matrix);

        float[] newRight = entity.getRightRotation();

        Vector3f rightRotationChange = new Quaternionf(oldRight[0], oldRight[1], oldRight[2], oldRight[3])
                .difference(new Quaternionf(newRight[0], newRight[1], newRight[2], newRight[3]))
                .getEulerAnglesXYZ(new Vector3f());

        if (Math.abs(rightRotationChange.z) >= org.joml.Math.toRadians(45f)) {
            float rot = org.joml.Math.toRadians(-90f) * Math.signum(rightRotationChange.z);

            Quaternionf leftRotation = entity.getLeftRotationQuaternion();
            entity.setLeftRotation(leftRotation.rotateZ(-rot));

            Vector3F scale = entity.getScale();
            entity.setScale(scale.y, scale.x, scale.z);

            Quaternionf rightRotation = entity.getRightRotationQuaternion();
            entity.setRightRotation(rightRotation.rotateZ(rot));
        }

        if (oldTransformation.equals(entity.getTransformationMatrix())) return;
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
