package top.mrxiaom.hologram.vector.displays.utils.matrix;

import top.mrxiaom.hologram.vector.displays.utils.joml.Math;
import top.mrxiaom.hologram.vector.displays.utils.joml.Matrix3f;
import top.mrxiaom.hologram.vector.displays.utils.joml.Quaternionf;
import top.mrxiaom.hologram.vector.displays.utils.joml.Vector3f;

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

    public static org.joml.Quaternionf eigenvalueJacobi(org.joml.Matrix3f AtA, int numJacobiIterations) {
        Matrix3f fixed = new Matrix3f(AtA);
        Quaternionf result = eigenvalueJacobi(fixed, numJacobiIterations);
        AtA.set(fixed.toNative());
        return result.toNative();
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

    public static Decomposed svdDecompose(org.joml.Matrix3f A) {
        Matrix3f fixed = new Matrix3f(A);
        Decomposed result = svdDecompose(fixed);
        A.set(fixed.toNative());
        return result;
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
        return new Decomposed(quaternionf2.toNative(), vector3f.toNative(), quaternionf.conjugate().toNative());
    }

    public static Decomposed svdDecomposeRoot(Matrix3f matrix) {
        double[][] columns = {
                {matrix.m00, matrix.m01, matrix.m02},
                {matrix.m10, matrix.m11, matrix.m12},
                {matrix.m20, matrix.m21, matrix.m22}
        };
        double[][] right = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        for (int sweep = 0; sweep < 24; sweep++) {
            boolean changed = false;
            for (int p = 0; p < 2; p++) {
                for (int q = p + 1; q < 3; q++) {
                    double a = dot(columns[p], columns[p]);
                    double b = dot(columns[q], columns[q]);
                    double c = dot(columns[p], columns[q]);
                    if (c == 0 || java.lang.Math.abs(c) <= 1e-14 * java.lang.Math.sqrt(a * b)) continue;
                    double tau = (b - a) / (2 * c);
                    double t = java.lang.Math.copySign(1.0, tau)
                            / (java.lang.Math.abs(tau) + java.lang.Math.hypot(1, tau));
                    double cos = 1 / java.lang.Math.sqrt(1 + t * t);
                    double sin = cos * t;
                    rotateColumns(columns, p, q, cos, sin);
                    rotateColumns(right, p, q, cos, sin);
                    changed = true;
                }
            }
            if (!changed) break;
        }
        for (int p = 0; p < 2; p++) {
            for (int q = p + 1; q < 3; q++) {
                if (dot(columns[p], columns[p]) < dot(columns[q], columns[q])) {
                    double[] swap = columns[p]; columns[p] = columns[q]; columns[q] = swap;
                    swap = right[p]; right[p] = right[q]; right[q] = swap;
                }
            }
        }
        if (dot(cross(right[0], right[1]), right[2]) < 0) {
            for (int i = 0; i < 3; i++) {
                right[2][i] = -right[2][i];
                columns[2][i] = -columns[2][i];
            }
        }
        double s0 = java.lang.Math.sqrt(dot(columns[0], columns[0]));
        if (s0 == 0) return new Decomposed(new org.joml.Quaternionf(), new org.joml.Vector3f(), new org.joml.Quaternionf());
        double[] u0 = columns[0].clone();
        for (int i = 0; i < 3; i++) u0[i] /= s0;
        double[] u1 = columns[1].clone();
        double projection = dot(u0, u1);
        for (int i = 0; i < 3; i++) u1[i] -= projection * u0[i];
        double s1 = java.lang.Math.sqrt(dot(u1, u1));
        if (s1 <= s0 * 1e-12) {
            int axis = java.lang.Math.abs(u0[0]) < java.lang.Math.abs(u0[1]) ? 0 : 1;
            if (java.lang.Math.abs(u0[2]) < java.lang.Math.abs(u0[axis])) axis = 2;
            u1 = new double[3];
            u1[axis] = 1;
            projection = u0[axis];
            for (int i = 0; i < 3; i++) u1[i] -= projection * u0[i];
        }
        double length = java.lang.Math.sqrt(dot(u1, u1));
        for (int i = 0; i < 3; i++) u1[i] /= length;
        double[] u2 = cross(u0, u1);
        double s2 = dot(columns[2], u2);
        Quaternionf leftRotation = new Quaternionf().setFromNormalized(fromColumns(u0, u1, u2)).normalize();
        Quaternionf rightRotation = new Quaternionf().setFromNormalized(fromColumns(right[0], right[1], right[2])).normalize().conjugate();
        return new Decomposed(leftRotation.toNative(), new org.joml.Vector3f((float) s0, (float) s1, (float) s2), rightRotation.toNative());
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    private static void rotateColumns(double[][] columns, int p, int q, double cos, double sin) {
        for (int i = 0; i < 3; i++) {
            double a = columns[p][i], b = columns[q][i];
            columns[p][i] = cos * a - sin * b;
            columns[q][i] = sin * a + cos * b;
        }
    }

    private static Matrix3f fromColumns(double[] a, double[] b, double[] c) {
        return new Matrix3f((float) a[0], (float) a[1], (float) a[2],
                (float) b[0], (float) b[1], (float) b[2], (float) c[0], (float) c[1], (float) c[2]);
    }
}
