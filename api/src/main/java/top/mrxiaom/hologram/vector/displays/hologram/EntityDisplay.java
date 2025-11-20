package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.util.Quaternion4f;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.utils.Vector3F;
import top.mrxiaom.hologram.vector.displays.utils.matrix.Decomposed;
import top.mrxiaom.hologram.vector.displays.utils.matrix.MatrixUtil;

public abstract class EntityDisplay<This extends AbstractEntity<This>> extends AbstractEntity<This> {
    private Matrix4f matrix;

    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    protected int interpolationDurationRotation = 10;
    protected int interpolationDurationTransformation = 10;
    protected float viewRange = 1.0f;
    protected Display.Billboard billboard = Display.Billboard.CENTER;
    protected int brightnessOverride = -1;
    protected EntityDisplay(RenderMode renderMode) {
        this(renderMode, IEntityIdProvider.DEFAULT);
    }
    protected EntityDisplay(RenderMode renderMode, IEntityIdProvider provider) {
        super(renderMode, provider);
        this.composeMatrix();
    }

    protected void applyDisplayMeta(AbstractDisplayMeta meta) {
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(toVector3f(this.translation));
        meta.setScale(toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setViewRange(this.viewRange);
        meta.setBrightnessOverride(this.brightnessOverride);
        meta.setRightRotation(this.rightRotation);
        meta.setLeftRotation(this.leftRotation);
    }

    @Override
    public void spawn(@NotNull Location location) {
        // 去掉 yaw 和 pitch 信息，以防应用了错误的旋转变换
        super.spawn(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public This teleport(@NotNull Location location) {
        // 去掉 yaw 和 pitch 信息，以防应用了错误的旋转变换
        return super.teleport(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
    }

    public This setTransformationMatrix(@NotNull Matrix4f matrix) {
        // com.mojang.math.Transformation#ensureDecomposed()
        float f = 1.0F / matrix.m33();

        Decomposed decomposed = MatrixUtil.svdDecompose(new Matrix3f(matrix).scale(f));
        this.translation = matrix.getTranslation(new Vector3f()).mul(f);
        this.leftRotation = decomposed.getLeftRotation();
        this.scale = decomposed.getScale();
        this.rightRotation = decomposed.getRightRotation();
        this.composeMatrix();
        return $this();
    }

    @NotNull
    public Matrix4f getTransformationMatrix() {
        return this.matrix;
    }

    protected void composeMatrix() {
        // com.mojang.math.Transformation#compose(Vector3f, Quaternionf, Vector3f, Quaternionf)
        Matrix4f matrix4f = new Matrix4f();
        if (translation != null) {
            matrix4f.translation(translation);
        }
        if (leftRotation != null) {
            matrix4f.rotate(leftRotation.getX(), leftRotation.getY(), leftRotation.getZ(), leftRotation.getW());
        }
        if (scale != null) {
            matrix4f.scale(scale);
        }
        if (rightRotation != null) {
            matrix4f.rotate(rightRotation.getX(), rightRotation.getY(), rightRotation.getZ(), rightRotation.getW());
        }
        this.matrix = matrix4f;
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public This setScale(float scale) {
        return setScale(scale, scale, scale);
    }

    public This setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        this.composeMatrix();
        return $this();
    }

    public This setScale(Vector3f scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        this.composeMatrix();
        return $this();
    }

    public This setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        this.composeMatrix();
        return $this();
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public This setLeftRotation(Quaternionf r) {
        return setLeftRotation(r.x, r.y, r.z, r.w);
    }

    public This setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        this.composeMatrix();
        return $this();
    }

    public This setRightRotation(Quaternionf r) {
        return setRightRotation(r.x, r.y, r.z, r.w);
    }

    public This setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        this.composeMatrix();
        return $this();
    }

    public This setLeftRotation(float[] rotation) {
        this.leftRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        this.composeMatrix();
        return $this();
    }

    public This setRightRotation(float[] rotation) {
        this.rightRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        this.composeMatrix();
        return $this();
    }

    public float[] getLeftRotation() {
        Quaternion4f r = leftRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public Quaternionf getLeftRotationQuaternion() {
        Quaternion4f r = leftRotation;
        return new Quaternionf(r.getX(), r.getY(), r.getZ(), r.getW());
    }

    public float[] getRightRotation() {
        Quaternion4f r = rightRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public Quaternionf getRightRotationQuaternion() {
        Quaternion4f r = rightRotation;
        return new Quaternionf(r.getX(), r.getY(), r.getZ(), r.getW());
    }

    public This setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        this.composeMatrix();
        return $this();
    }

    public This setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        this.composeMatrix();
        return $this();
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public This setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return $this();
    }
    public int getInterpolationDurationRotation() {
        return interpolationDurationRotation;
    }

    public This setInterpolationDurationRotation(int interpolationDurationRotation) {
        this.interpolationDurationRotation = interpolationDurationRotation;
        return $this();
    }

    public int getInterpolationDurationTransformation() {
        return interpolationDurationTransformation;
    }

    public This setInterpolationDurationTransformation(int interpolationDurationTransformation) {
        this.interpolationDurationTransformation = interpolationDurationTransformation;
        return $this();
    }

    public float getViewRange() {
        return viewRange;
    }

    public This setViewRange(float viewRange) {
        this.viewRange = viewRange;
        return $this();
    }

    public int getBrightnessOverride() {
        return brightnessOverride;
    }

    public This setBrightnessOverride(int brightnessOverride) {
        this.brightnessOverride = brightnessOverride;
        return $this();
    }
}
