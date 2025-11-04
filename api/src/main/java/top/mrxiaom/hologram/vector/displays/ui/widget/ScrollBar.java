package top.mrxiaom.hologram.vector.displays.ui.widget;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.ClickMeta;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

/**
 * 滚动条控件
 */
public class ScrollBar extends TextElement<ScrollBar> implements EntityTextDisplayWrapper<ScrollBar> {
    private int division;
    private double progress;
    private int foregroundColor;
    private float sliderWidth, sliderHeight;
    private final EntityTextDisplay hologramMark;
    private float markTextWidth;
    private double markWidth, markHeight;
    private final float spaceWidth;

    /**
     * 滚动条控件
     * @param id 元素ID
     */
    public ScrollBar(@NotNull String id) {
        this(id, 10, 10.0f, 2f);
    }

    /**
     * 滚动条控件
     * @param id 元素ID
     * @param division 分为多少块（确定滑动按钮大小） TODO: 这个参数对应的功能还没有做完
     * @param sliderWidth 滚动条宽度
     * @param sliderHeight 滚动条高度
     */
    public ScrollBar(@NotNull String id, int division, float sliderWidth, float sliderHeight) {
        super(id);
        this.hologramMark = createHologram()
                .setText(Component.text("                "))
                .setShadow(false);
        this.spaceWidth = HologramFont.getTextRenderer().getWidth(hologramMark.getTextAsComponent());
        this.hologramMark.setParent(getHologram());
        this.division = division;
        this.setBackgroundColor(0x20FFFFFF);
        this.setForegroundColor(0xFFFFFFFF);
        this.setShadow(false);
        this.setZIndex(15);
        this.setSliderSize(sliderWidth, sliderHeight);
    }

    @Override
    public ScrollBar setFullBrightness() {
        this.hologramMark.setBrightnessOverride(15 << 4 | 15 << 20);
        return super.setFullBrightness();
    }

    @Override
    public void init() {
        updateText();
        super.init();
        hologramMark.setLeftRotation(getRotation());
        HologramAPI.getHologram().spawn(hologramMark, decideMarkLocation());
    }

    @Override
    public void updateLocation() {
        super.updateLocation();
        hologramMark.teleport(decideMarkLocation());
    }


    protected double[] decideMarkLocationRaw(double pX, double pY) {
        Terminal<?> terminal = getTerminal();
        // 计算世界相对坐标
        double charScale = HologramFont.getCharScale();
        double x = pX * charScale;
        double y = pY * charScale;
        // 获取终端背景的参数
        double rootWidth = terminal.getWidth();
        double rootHeight = terminal.getHeight();
        Location rootLocation = terminal.getLocation();
        double rootX = rootLocation.getX();
        double rootY = rootLocation.getY();
        double z = rootLocation.getZ() + (0.001 * (getZIndex() + 1));

        // 根据排列方式的不同，计算在世界上的初始坐标
        return getAlign().get(rootX, rootY, z, rootWidth, rootHeight, x, y, markWidth, markHeight);
    }

    /**
     * 根据 terminal 的旋转关系，计算出这个子元素的悬浮字应当出现在什么坐标处
     */
    protected Location decideMarkLocation() {
        double x = switch (getAlign()) {
            case LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM -> getX() + (markTextWidth / 2.0);
            case CENTER_TOP, CENTER, CENTER_BOTTOM -> getX() - (getTextWidth() / 2.0) + (markTextWidth / 2.0);
            case RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM -> getX() - getTextWidth() - (markTextWidth / 2.0);
        };
        double[] raw = decideMarkLocationRaw(x + progress * (getTextWidth() - markTextWidth), getY());

        // 根据终端旋转量进行坐标变换
        float[] ar = getAdditionalRotation();
        if (ar != null) {
            double[] rotated = QuaternionUtils.rotateChildrenToDouble(decideLocationRaw(getX(), getY()), ar, raw);
            return getTerminal().getRotatedLoc(rotated);
        } else {
            return getTerminal().getRotatedLoc(raw);
        }
    }

    /**
     * 获取滚动条的宽度
     */
    public float getSliderWidth() {
        return sliderWidth;
    }

    /**
     * 获取滚动条的高度
     */
    public float getSliderHeight() {
        return sliderHeight;
    }

    /**
     * 设置滚动条的大小，使用文字参考系
     * @param sliderWidth 滚动条宽度
     * @param sliderHeight 滚动条高度
     */
    public ScrollBar setSliderSize(float sliderWidth, float sliderHeight) {
        this.sliderWidth = sliderWidth;
        this.sliderHeight = sliderHeight;
        this.updateText();
        return this;
    }

    /**
     * 获取滑块按钮的颜色
     */
    public int getForegroundColor() {
        return foregroundColor;
    }

    /**
     * 设置滑块按钮的颜色
     */
    public ScrollBar setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        this.hologramMark.setBackgroundColor(foregroundColor);
        return this;
    }

    /**
     * 获取滚动条的分块份数，用于确定滚动条的滑块按钮宽度
     */
    public int getDivision() {
        return division;
    }

    /**
     * 设置滚动条的分块份数，用于确定滚动条的滑块按钮宽度
     */
    public void setDivision(int division) {
        this.division = division;
        this.updateText();
    }

    /**
     * 获取当前滚动值
     */
    public double getProgress() {
        return progress;
    }

    /**
     * 获取当前滚动值
     */
    public void setProgress(double progress) {
        double newProgress = Math.max(0.0, Math.min(1.0, progress));
        if (newProgress != this.progress) {
            this.progress = newProgress;
            this.updateText();
            this.updateLocation();
        }
    }

    private void updateText() {
        hologram.setText(hologramMark.getTextAsComponent());

        float scaleX = HologramUtils.calculateScale(spaceWidth, this.sliderWidth);
        float scaleY = HologramUtils.calculateScale(HologramUtils.LINE_HEIGHT, this.sliderHeight);
        this.setScale(scaleX, scaleY);

        this.markTextWidth = this.sliderWidth / this.division;
        float spaceScaleX = HologramUtils.calculateScale(spaceWidth, this.markTextWidth);
        markWidth = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        markHeight = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        hologramMark.setScale(spaceScaleX, scaleY, 1.0f);
    }

    @Override
    public void performClick(ClickMeta meta) {
        // TODO: 需要解决一个问题，meta.getWhereClicked 没有应用额外旋转
        meta.getPlayer().sendMessage("点击位置: " + meta.getWhereClicked());
        setProgress((meta.getWhereClicked().getX() + (getTextWidth() / 2.0)) / getTextWidth());
    }

    @Override
    public void update() {
        hologramMark.update();
        super.update();
    }

    @Override
    public void dispose() {
        hologramMark.removeAllViewers();
        HologramAPI.getHologram().remove(hologramMark);
        super.dispose();
    }
}
