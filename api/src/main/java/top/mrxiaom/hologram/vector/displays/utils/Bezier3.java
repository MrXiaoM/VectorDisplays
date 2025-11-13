package top.mrxiaom.hologram.vector.displays.utils;

/**
 * 贝塞尔曲线动画机
 */
public class Bezier3 {
    private final float px0, py0, px1, py1, px2, py2, px3, py3;
    private long startTime, duration, endTime;

    public Bezier3(float px0, float py0, float px1, float py1, float px2, float py2, float px3, float py3) {
        this.px0 = px0;
        this.py0 = py0;
        this.px1 = px1;
        this.py1 = py1;
        this.px2 = px2;
        this.py2 = py2;
        this.px3 = px3;
        this.py3 = py3;
    }

    /**
     * 获取动画开始时间戳 (毫秒)
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 设置动画开始时间戳 (毫秒)
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取动画持续时间 (毫秒)
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 设置动画持续时间 (毫秒)
     */
    public void setDuration(long duration) {
        if (startTime == 0L) {
            throw new IllegalStateException("还没有设置 startTime");
        }
        this.duration = duration;
        this.endTime = startTime + duration;
    }

    /**
     * 获取当前动画是否已播放结束
     */
    public boolean hasEnd() {
        return System.currentTimeMillis() > endTime;
    }

    /**
     * 获取时间对应点的 (x, y) 坐标
     * @see Bezier3#getPoint(float)
     */
    public float[] getPoint() {
        float v = (float) (System.currentTimeMillis() - startTime) / duration;
        return getPoint(px0 + ((px3 - px0) * v));
    }

    /**
     * 通过输入时间，获取时间对应点的 (x, y) 坐标
     * @param t 输入时间，范围 <code>[0, 1]</code>，超出范围自动裁剪
     * @return <code>x = result[0], y = result[1]</code>
     */
    public float[] getPoint(float t) {
        if (t < 0.0f) t = 0.0f;
        if (t > 1.0f) t = 1.0f;
        // 三次方公式
        // B(t) = P0(1-t)^3 + 3P1t(1-t)^2 + 3P2t^2(1-t) + P3t^3
        // B(t) = a*P0 + b*P1 + c*P2 + d*P3
        // 将各点的 x, y 坐标分别代入 P0 P1 P2 P3 即可算出最终 x, y 坐标值
        float a = (1.0f - t) * (1.0f - t) * (1.0f - t);
        float b = 3.0f * t * (1.0f - t) * (1.0f - t);
        float c = 3.0f * t * t * (1.0f - t);
        float d = t * t * t;
        return new float[] {
                a * px0 + b * px1 + c * px2 + d * px3,
                a * py0 + b * py1 + c * py2 + d * py3
        };
    }

    /**
     * 与 CSS 中的 <code>cubic-bezier(a,b,c,d)</code> 用法相同
     *
     * @see <a href="https://cubic-bezier.com/">cubic-bezier.com</a>
     */
    public static Bezier3 cubicBezier(float a, float b, float c, float d) {
        return new Bezier3(0, 0, a, b, c, d, 1, 1);
    }
}
