package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.util.Quaternion4f;
import org.bukkit.entity.Display;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.utils.Vector3F;

public abstract class EntityDisplay<This extends AbstractEntity<This>> extends AbstractEntity<This> {
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    protected int interpolationDurationRotation = 10;
    protected int interpolationDurationTransformation = 10;
    protected double viewRange = 1.0;
    protected Display.Billboard billboard = Display.Billboard.CENTER;
    protected int brightnessOverride = -1;
    protected EntityDisplay(RenderMode renderMode) {
        super(renderMode);
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public This setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return $this();
    }

    public This setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return $this();
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public This setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return $this();
    }

    public This setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return $this();
    }

    public This setLeftRotation(float[] rotation) {
        this.leftRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        return $this();
    }

    public This setRightRotation(float[] rotation) {
        this.rightRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        return $this();
    }

    public float[] getLeftRotation() {
        Quaternion4f r = leftRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public float[] getRightRotation() {
        Quaternion4f r = rightRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public This setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return $this();
    }

    public This setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
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

    public double getViewRange() {
        return viewRange;
    }

    public This setViewRange(double viewRange) {
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
