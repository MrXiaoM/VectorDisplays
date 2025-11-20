package top.mrxiaom.hologram.vector.displays.utils.matrix;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

/**
 * org.mojang.math.GivensParameters
 */
public record GivensParameters(float sinHalf, float cosHalf) {
    public static GivensParameters fromUnnormalized(float a, float b) {
        float f = Math.invsqrt(a * a + b * b);
        return new GivensParameters(f * a, f * b);
    }

    public static GivensParameters fromPositiveAngle(float radians) {
        float f = Math.sin(radians / 2.0F);
        float g = Math.cosFromSin(f, radians / 2.0F);
        return new GivensParameters(f, g);
    }

    public GivensParameters inverse() {
        return new GivensParameters(-this.sinHalf, this.cosHalf);
    }

    public Quaternionf aroundX(Quaternionf quaternionf) {
        return quaternionf.set(this.sinHalf, 0.0F, 0.0F, this.cosHalf);
    }

    public Quaternionf aroundY(Quaternionf quaternionf) {
        return quaternionf.set(0.0F, this.sinHalf, 0.0F, this.cosHalf);
    }

    public Quaternionf aroundZ(Quaternionf quaternionf) {
        return quaternionf.set(0.0F, 0.0F, this.sinHalf, this.cosHalf);
    }

    public float cos() {
        return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf;
    }

    public float sin() {
        return 2.0F * this.sinHalf * this.cosHalf;
    }

    public Matrix3f aroundX(Matrix3f matrix3f) {
        matrix3f.m01 = 0.0F;
        matrix3f.m02 = 0.0F;
        matrix3f.m10 = 0.0F;
        matrix3f.m20 = 0.0F;
        float f = this.cos();
        float g = this.sin();
        matrix3f.m11 = f;
        matrix3f.m22 = f;
        matrix3f.m12 = g;
        matrix3f.m21 = -g;
        matrix3f.m00 = 1.0F;
        return matrix3f;
    }

    public Matrix3f aroundY(Matrix3f matrix3f) {
        matrix3f.m01 = 0.0F;
        matrix3f.m10 = 0.0F;
        matrix3f.m12 = 0.0F;
        matrix3f.m21 = 0.0F;
        float f = this.cos();
        float g = this.sin();
        matrix3f.m00 = f;
        matrix3f.m22 = f;
        matrix3f.m02 = -g;
        matrix3f.m20 = g;
        matrix3f.m11 = 1.0F;
        return matrix3f;
    }

    public Matrix3f aroundZ(Matrix3f matrix3f) {
        matrix3f.m02 = 0.0F;
        matrix3f.m12 = 0.0F;
        matrix3f.m20 = 0.0F;
        matrix3f.m21 = 0.0F;
        float f = this.cos();
        float g = this.sin();
        matrix3f.m00 = f;
        matrix3f.m11 = f;
        matrix3f.m01 = g;
        matrix3f.m10 = -g;
        matrix3f.m22 = 1.0F;
        return matrix3f;
    }
}
