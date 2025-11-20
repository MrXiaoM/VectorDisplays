package top.mrxiaom.hologram.vector.displays.utils.matrix;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * org.mojang.math.MatrixUtil
 */
public class MatrixUtil {
    private static final float G = 3.0F + 2.0F * Math.sqrt(2.0F);
    private static final GivensParameters PI_4 = GivensParameters.fromPositiveAngle((float) (java.lang.Math.PI / 4));

    private MatrixUtil() {
    }

    private static GivensParameters approxGivensQuat(float a11, float a12, float a22) {
        float f = 2.0F * (a11 - a22);
        return G * a12 * a12 < f * f ? GivensParameters.fromUnnormalized(a12, f) : PI_4;
    }

    private static GivensParameters qrGivensQuat(float a1, float a2) {
        float f = (float)java.lang.Math.hypot((double)a1, (double)a2);
        float g = f > 1.0E-6F ? a2 : 0.0F;
        float h = Math.abs(a1) + Math.max(f, 1.0E-6F);
        if (a1 < 0.0F) {
            float i = g;
            g = h;
            h = i;
        }

        return GivensParameters.fromUnnormalized(g, h);
    }

    private static void similarityTransform(Matrix3f X, Matrix3f A) {
        X.mul(A);
        A.transpose();
        A.mul(X);
        X.set(A);
    }

    private static void stepJacobi(Matrix3f AtA, Matrix3f matrix3f, Quaternionf quaternionf, Quaternionf quaternionf2) {
        if (AtA.m01 * AtA.m01 + AtA.m10 * AtA.m10 > 1.0E-6F) {
            GivensParameters givensParameters = approxGivensQuat(AtA.m00, 0.5F * (AtA.m01 + AtA.m10), AtA.m11);
            Quaternionf quaternionf3 = givensParameters.aroundZ(quaternionf);
            quaternionf2.mul(quaternionf3);
            givensParameters.aroundZ(matrix3f);
            similarityTransform(AtA, matrix3f);
        }

        if (AtA.m02 * AtA.m02 + AtA.m20 * AtA.m20 > 1.0E-6F) {
            GivensParameters givensParameters2 = approxGivensQuat(AtA.m00, 0.5F * (AtA.m02 + AtA.m20), AtA.m22).inverse();
            Quaternionf quaternionf4 = givensParameters2.aroundY(quaternionf);
            quaternionf2.mul(quaternionf4);
            givensParameters2.aroundY(matrix3f);
            similarityTransform(AtA, matrix3f);
        }

        if (AtA.m12 * AtA.m12 + AtA.m21 * AtA.m21 > 1.0E-6F) {
            GivensParameters givensParameters3 = approxGivensQuat(AtA.m11, 0.5F * (AtA.m12 + AtA.m21), AtA.m22);
            Quaternionf quaternionf5 = givensParameters3.aroundX(quaternionf);
            quaternionf2.mul(quaternionf5);
            givensParameters3.aroundX(matrix3f);
            similarityTransform(AtA, matrix3f);
        }
    }

    public static Quaternionf eigenvalueJacobi(Matrix3f AtA, int numJacobiIterations) {
        Quaternionf quaternionf = new Quaternionf();
        Matrix3f matrix3f = new Matrix3f();
        Quaternionf quaternionf2 = new Quaternionf();

        for (int i = 0; i < numJacobiIterations; i++) {
            stepJacobi(AtA, matrix3f, quaternionf2, quaternionf);
        }

        quaternionf.normalize();
        return quaternionf;
    }

    public static Decomposed svdDecompose(Matrix3f A) {
        Matrix3f matrix3f = new Matrix3f(A);
        matrix3f.transpose();
        matrix3f.mul(A);
        Quaternionf quaternionf = eigenvalueJacobi(matrix3f, 5);
        float f = matrix3f.m00;
        float g = matrix3f.m11;
        boolean bl = (double)f < 1.0E-6;
        boolean bl2 = (double)g < 1.0E-6;
        Matrix3f matrix3f3 = A.rotate(quaternionf);
        Quaternionf quaternionf2 = new Quaternionf();
        Quaternionf quaternionf3 = new Quaternionf();
        GivensParameters givensParameters;
        if (bl) {
            givensParameters = qrGivensQuat(matrix3f3.m11, -matrix3f3.m10);
        } else {
            givensParameters = qrGivensQuat(matrix3f3.m00, matrix3f3.m01);
        }

        Quaternionf quaternionf4 = givensParameters.aroundZ(quaternionf3);
        Matrix3f matrix3f4 = givensParameters.aroundZ(matrix3f);
        quaternionf2.mul(quaternionf4);
        matrix3f4.transpose().mul(matrix3f3);
        if (bl) {
            givensParameters = qrGivensQuat(matrix3f4.m22, -matrix3f4.m20);
        } else {
            givensParameters = qrGivensQuat(matrix3f4.m00, matrix3f4.m02);
        }

        givensParameters = givensParameters.inverse();
        Quaternionf quaternionf5 = givensParameters.aroundY(quaternionf3);
        Matrix3f matrix3f5 = givensParameters.aroundY(matrix3f3);
        quaternionf2.mul(quaternionf5);
        matrix3f5.transpose().mul(matrix3f4);
        if (bl2) {
            givensParameters = qrGivensQuat(matrix3f5.m22, -matrix3f5.m21);
        } else {
            givensParameters = qrGivensQuat(matrix3f5.m11, matrix3f5.m12);
        }

        Quaternionf quaternionf6 = givensParameters.aroundX(quaternionf3);
        Matrix3f matrix3f6 = givensParameters.aroundX(matrix3f4);
        quaternionf2.mul(quaternionf6);
        matrix3f6.transpose().mul(matrix3f5);
        Vector3f vector3f = new Vector3f(matrix3f6.m00, matrix3f6.m11, matrix3f6.m22);
        return new Decomposed(quaternionf2, vector3f, quaternionf.conjugate());
    }
}
