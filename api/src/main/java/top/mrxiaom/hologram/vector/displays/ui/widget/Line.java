package top.mrxiaom.hologram.vector.displays.ui.widget;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.ui.EnumAlign;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

import java.math.BigDecimal;

public class Line extends TextElement<Line> {
    private final double spaceWidth = HologramFont.getTextRenderer().getWidth(Component.text(" "));
    private double x1, y1, x2, y2;
    private double thickness = 0.5;
    private float[] rotation;
    public Line(@NotNull String id) {
        super(id);
        setShadow(false);
        setBackgroundColor(0x80FFFFFF);
    }

    /**
     * 获取线条第一个端点的 <code>x</code> 坐标
     */
    public double getX1() {
        return x1;
    }

    /**
     * 获取线条第一个端点的 <code>y</code> 坐标
     */
    public double getY1() {
        return y1;
    }

    /**
     * 获取线条第二个端点的 <code>x</code> 坐标
     */
    public double getX2() {
        return x2;
    }

    /**
     * 获取线条第二个端点的 <code>y</code> 坐标
     */
    public double getY2() {
        return y2;
    }

    /**
     * 设置线条的第一个端点
     */
    public Line setPos1(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
        return this;
    }

    /**
     * 设置线条的第一个端点
     */
    public Line setPos1(Element<?, ?> element) {
        setPos1(element.getX(), element.getY());
        return this;
    }

    /**
     * 设置线条的第二个端点
     */
    public Line setPos2(double x2, double y2) {
        this.x2 = x2;
        this.y2 = y2;
        return this;
    }

    /**
     * 设置线条的第二个端点
     */
    public Line setPos2(Element<?, ?> element) {
        setPos2(element.getX(), element.getY());
        return this;
    }

    /**
     * 设置线条宽度
     */
    public Line setThickness(double thickness) {
        this.thickness = thickness;
        return this;
    }

    @Override
    public float[] getRotation() {
        return rotation == null ? super.getRotation() : rotation;
    }

    /**
     * 根据端点，计算元素位置、旋转角度、悬浮字文本内容等
     */
    public void updatePos() {
        // 将元素位置设为线段中点
        double x = (x1 + x2) / 2.0;
        double y = (y1 + y2) / 2.0;
        super.setPos(x, y);
        super.setAlign(EnumAlign.CENTER);

        // 根据两点算出来线段长度，单位跟字体相同
        double length = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));

        // 计算四个点，AB 是原线段的端点，CD 是旋转后的线段端点
        Terminal<?> terminal = getTerminal();
        float[] locA = terminal.getRotated(decideLocationRaw(x - length / 2.0, y));
        float[] locB = terminal.getRotated(decideLocationRaw(x + length / 2.0, y));
        float[] locC = terminal.getRotated(decideLocationRaw(x1, y1));
        float[] locD = terminal.getRotated(decideLocationRaw(x2, y2));

        // 计算旋转量
        // 先进行父组件的相对坐标转换旋转 (getRotation)
        // 再根据 AB、CD 线段进行旋转 (calculateRotation)
        this.rotation = QuaternionUtils.multiplyF(
                QuaternionUtils.calculateRotation(locA, locB, locC, locD),
                getParentRotation()
        );

        // 根据 thickness (宽度) 与悬浮字单行标准高度的比值，计算缩放大小
        double scale = thickness / HologramUtils.LINE_HEIGHT;
        this.setScale(new BigDecimal(String.format("%.8f", scale)).floatValue());
        // 计算最少需要的空格数量
        int spaceCount = (int) Math.floor(length / (this.spaceWidth * scale));
        // 最大长度管够
        hologram.setMaxLineWidth(1000);
        // 设置空格字符
        hologram.setText(Component.text(" ".repeat(spaceCount)));
    }

    @Override
    public void init() {
        updatePos();
        super.init();
    }

    /**
     * @see Line#getX1()
     * @see Line#getX2()
     */
    @Override
    @Deprecated
    public double getX() {
        return super.getX();
    }

    /**
     * @see Line#setPos1(double, double)
     * @see Line#setPos2(double, double)
     */
    @Override
    @Deprecated
    public Line setX(double x) {
        return super.setX(x);
    }

    /**
     * @see Line#getY1()
     * @see Line#getY2()
     */
    @Override
    @Deprecated
    public double getY() {
        return super.getY();
    }

    /**
     * @see Line#setPos1(double, double)
     * @see Line#setPos2(double, double)
     */
    @Override
    @Deprecated
    public Line setY(double y) {
        return super.setY(y);
    }

    /**
     * @see Line#setPos1(double, double)
     * @see Line#setPos2(double, double)
     */
    @Override
    @Deprecated
    public Line setPos(double x, double y) {
        return super.setPos(x, y);
    }

    @Override
    @Deprecated
    public float @Nullable [] getAdditionalRotation() {
        return null;
    }

    @Override
    @Deprecated
    public Line setAdditionalRotation(float @Nullable [] additionalRotation) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public Line setAdditionalRotation(float yaw, float pitch, float roll) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public Line setAdditionalRotation(float yaw, float pitch) {
        throw new UnsupportedOperationException();
    }
}
