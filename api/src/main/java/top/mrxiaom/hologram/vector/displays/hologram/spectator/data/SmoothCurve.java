package top.mrxiaom.hologram.vector.displays.hologram.spectator.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * AI 编写的三次埃尔米特样条（Cubic Hermite Spline）平滑曲线算法
 */
public class SmoothCurve {
    private final List<Node> nodes;
    private final Node firstNode;
    private final Node lastNode;
    private final int size;

    // 位置切线
    private final double[] mX, mY, mZ;
    // 方向向量切线
    private final double[] mVX, mVY, mVZ;

    public SmoothCurve(Collection<Node> inputNodes) {
        if (inputNodes == null || inputNodes.isEmpty()) {
            throw new IllegalArgumentException("节点列表不能为空");
        }

        // 复制一份列表并按 timelineMills 从小到大排序，确保时间轴正确
        this.nodes = new ArrayList<>(inputNodes);
        this.nodes.sort(Comparator.comparingLong(Node::timelineMills));
        this.size = nodes.size();
        this.firstNode = nodes.get(0);
        this.lastNode = nodes.get(size - 1);

        this.mX = new double[size];
        this.mY = new double[size];
        this.mZ = new double[size];
        this.mVX = new double[size];
        this.mVY = new double[size];
        this.mVZ = new double[size];

        calculateTangents();
    }

    public Node firstNode() {
        return firstNode;
    }

    public Node lastNode() {
        return lastNode;
    }

    // ==================== 核心：计算切线 ====================

    private void calculateTangents() {
        if (size < 2) return;

        // 获取归一化后的方向向量分量
        double[] nvx = new double[size];
        double[] nvy = new double[size];
        double[] nvz = new double[size];
        for (int i = 0; i < size; i++) {
            Node n = nodes.get(i);
            double len = length(n.vX(), n.vY(), n.vZ());
            if (len < 1e-12) {
                // 方向向量长度为 0，使用默认朝向
                nvx[i] = 0; nvy[i] = 0; nvz[i] = 1;
            } else {
                nvx[i] = n.vX() / len;
                nvy[i] = n.vY() / len;
                nvz[i] = n.vZ() / len;
            }
        }

        // 中间节点：中心差分
        for (int i = 1; i < size - 1; i++) {
            double dt = nodes.get(i + 1).timelineMills() - nodes.get(i - 1).timelineMills();
            if (dt == 0) continue;

            Node prev = nodes.get(i - 1);
            Node next = nodes.get(i + 1);

            // 位置切线
            mX[i] = (next.x() - prev.x()) / dt;
            mY[i] = (next.y() - prev.y()) / dt;
            mZ[i] = (next.z() - prev.z()) / dt;

            // 方向向量切线
            mVX[i] = (nvx[i + 1] - nvx[i - 1]) / dt;
            mVY[i] = (nvy[i + 1] - nvy[i - 1]) / dt;
            mVZ[i] = (nvz[i + 1] - nvz[i - 1]) / dt;
        }

        // 起点：前向差分
        {
            double dt = nodes.get(1).timelineMills() - nodes.get(0).timelineMills();
            if (dt != 0) {
                Node n0 = nodes.get(0), n1 = nodes.get(1);
                mX[0] = (n1.x() - n0.x()) / dt;
                mY[0] = (n1.y() - n0.y()) / dt;
                mZ[0] = (n1.z() - n0.z()) / dt;

                mVX[0] = (nvx[1] - nvx[0]) / dt;
                mVY[0] = (nvy[1] - nvy[0]) / dt;
                mVZ[0] = (nvz[1] - nvz[0]) / dt;
            }
        }

        // 终点：后向差分
        {
            int e = size - 1;
            double dt = nodes.get(e).timelineMills() - nodes.get(e - 1).timelineMills();
            if (dt != 0) {
                Node nE = nodes.get(e), nP = nodes.get(e - 1);
                mX[e] = (nE.x() - nP.x()) / dt;
                mY[e] = (nE.y() - nP.y()) / dt;
                mZ[e] = (nE.z() - nP.z()) / dt;

                mVX[e] = (nvx[e] - nvx[e - 1]) / dt;
                mVY[e] = (nvy[e] - nvy[e - 1]) / dt;
                mVZ[e] = (nvz[e] - nvz[e - 1]) / dt;
            }
        }
    }

    /**
     * 获取指定时间进度的位置坐标
     * @return double[] {x, y, z}
     */
    public double[] evaluatePosition(long progress) {
        if (size == 1) {
            Node n = firstNode;
            return new double[]{n.x(), n.y(), n.z()};
        }
        if (progress <= firstNode.timelineMills()) {
            Node n = firstNode;
            return new double[]{n.x(), n.y(), n.z()};
        }
        if (progress >= lastNode.timelineMills()) {
            Node n = lastNode;
            return new double[]{n.x(), n.y(), n.z()};
        }

        int i = findInterval(progress);
        Node p0 = nodes.get(i);
        Node p1 = nodes.get(i + 1);
        double dt = p1.timelineMills() - p0.timelineMills();
        double t = (progress - p0.timelineMills()) / dt;

        return new double[]{
                hermite(p0.x(), p1.x(), mX[i] * dt, mX[i + 1] * dt, t),
                hermite(p0.y(), p1.y(), mY[i] * dt, mY[i + 1] * dt, t),
                hermite(p0.z(), p1.z(), mZ[i] * dt, mZ[i + 1] * dt, t)
        };
    }

    /**
     * 获取指定时间进度的方向向量（已归一化）
     * @return double[] {vX, vY, vZ}
     */
    public double[] evaluateDirection(long progress) {
        if (size == 1) {
            return normalizedDirection(firstNode);
        }
        if (progress <= firstNode.timelineMills()) {
            return normalizedDirection(firstNode);
        }
        if (progress >= lastNode.timelineMills()) {
            return normalizedDirection(lastNode);
        }

        int i = findInterval(progress);
        Node p0 = nodes.get(i);
        Node p1 = nodes.get(i + 1);
        double dt = p1.timelineMills() - p0.timelineMills();
        double t = (progress - p0.timelineMills()) / dt;

        // 获取归一化后的端点方向向量
        double[] d0 = normalizedDirection(p0);
        double[] d1 = normalizedDirection(p1);

        double rawVX = hermite(d0[0], d1[0], mVX[i] * dt, mVX[i + 1] * dt, t);
        double rawVY = hermite(d0[1], d1[1], mVY[i] * dt, mVY[i + 1] * dt, t);
        double rawVZ = hermite(d0[2], d1[2], mVZ[i] * dt, mVZ[i + 1] * dt, t);

        // 重新归一化，防止插值导致的长度偏差
        double len = length(rawVX, rawVY, rawVZ);
        if (len < 1e-12) {
            return d0; // 退化情况：返回起点方向
        }
        return new double[]{rawVX / len, rawVY / len, rawVZ / len};
    }

    /**
     * 同时获取位置和方向
     * @return double[] {x, y, z, vX, vY, vZ}
     */
    public double[] evaluate(long progress) {
        double[] pos = evaluatePosition(progress);
        double[] dir = evaluateDirection(progress);
        return new double[]{
                pos[0], pos[1], pos[2],
                dir[0], dir[1], dir[2]
        };
    }

    // ==================== 工具方法 ====================

    /**
     * 三次埃尔米特插值
     */
    private double hermite(double p0, double p1, double m0, double m1, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        double h00 =  2 * t3 - 3 * t2 + 1;
        double h10 =      t3 - 2 * t2 + t;
        double h01 = -2 * t3 + 3 * t2;
        double h11 =      t3 -     t2;
        return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1;
    }

    /**
     * 二分查找区间
     */
    private int findInterval(long progress) {
        int left = 0, right = size - 2, result = 0;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            if (nodes.get(mid).timelineMills() <= progress) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    private double[] normalizedDirection(Node n) {
        double len = length(n.vX(), n.vY(), n.vZ());
        if (len < 1e-12) {
            return new double[]{0, 0, 1};
        }
        return new double[]{n.vX() / len, n.vY() / len, n.vZ() / len};
    }
}
