package top.mrxiaom.hologram.vector.displays.utils.matrix;

import com.github.retrooper.packetevents.util.Quaternion4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Decomposed {
    private final Quaternionf leftRotation;
    private final Vector3f scale;
    private final Quaternionf rightRotation;

    public Decomposed(Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        this.leftRotation = leftRotation;
        this.scale = scale;
        this.rightRotation = rightRotation;
    }

    public Quaternionf getLeftRotationRaw() {
        return leftRotation;
    }

    public Quaternion4f getLeftRotation() {
        return new Quaternion4f(leftRotation.x, leftRotation.y, leftRotation.z, leftRotation.w);
    }

    public Vector3f getScale() {
        return scale;
    }

    public Quaternionf getRightRotationRaw() {
        return rightRotation;
    }

    public Quaternion4f getRightRotation() {
        return new Quaternion4f(rightRotation.x, rightRotation.y, rightRotation.z, rightRotation.w);
    }
}
