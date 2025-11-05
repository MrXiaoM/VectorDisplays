package top.mrxiaom.hologram.vector.displays.ui.widget;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.ClickMeta;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.ui.event.ValueChangedEvent;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

import java.util.function.Supplier;

/**
 * 进度条控件
 */
public class ProgressBar extends TextElement<ProgressBar> implements EntityTextDisplayWrapper<ProgressBar> {
    private double progress;
    private int foregroundColor;
    private float barWidth, barHeight;
    private final EntityTextDisplay hologramMark;
    private double markTextWidth;
    private double markWidth, markHeight;
    private final float spaceWidth;
    private ValueChangedEvent<ProgressBar, Double> progressChanged;

    /**
     * 进度条控件
     * @param id 元素ID
     */
    public ProgressBar(@NotNull String id) {
        this(id, 10.0f, 1f);
    }

    /**
     * 进度条控件
     * @param id 元素ID
     * @param barWidth 进度条宽度
     * @param barHeight 进度条高度
     */
    public ProgressBar(@NotNull String id, float barWidth, float barHeight) {
        super(id);
        this.hologramMark = createHologram()
                .setText(Component.text("                "))
                .setShadow(false);
        this.spaceWidth = HologramFont.getTextRenderer().getWidth(hologramMark.getTextAsComponent());
        this.hologramMark.setParent(getHologram());
        this.setBackgroundColor(0x20FFFFFF);
        this.setForegroundColor(0xFFFFFFFF);
        this.setShadow(false);
        this.setZIndex(15);
        this.setBarSize(barWidth, barHeight);
    }

    @Override
    public ProgressBar setFullBrightness() {
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
        System.out.printf("progress x: %.4f", x);
        double[] raw = decideMarkLocationRaw(x, getY());

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
     * 获取进度条的宽度
     */
    public float getBarWidth() {
        return barWidth;
    }

    /**
     * 获取进度条的高度
     */
    public float getBarHeight() {
        return barHeight;
    }

    /**
     * 设置进度条的大小，使用文字参考系
     * @param barWidth 进度条宽度
     * @param barHeight 进度条高度
     */
    public ProgressBar setBarSize(float barWidth, float barHeight) {
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.updateText();
        return this;
    }

    /**
     * 获取进度的颜色
     */
    public int getForegroundColor() {
        return foregroundColor;
    }

    /**
     * 设置进度的颜色
     */
    public ProgressBar setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        this.hologramMark.setBackgroundColor(foregroundColor);
        return this;
    }

    /**
     * 获取当前进度值
     */
    public double getProgress() {
        return progress;
    }

    /**
     * 获取当前进度值
     */
    public ProgressBar setProgress(double progress) {
        double newProgress = Math.max(0.0, Math.min(1.0, progress));
        System.out.printf("set progress %.2f%%\n", newProgress * 100.0);
        if (newProgress != this.progress) {
            double oldProgress = this.progress;
            this.progress = newProgress;
            if (this.progressChanged != null) {
                this.progressChanged.perform(oldProgress, newProgress, this);
            }
            this.updateText();
            if (!hologram.isDead()) {
                this.updateLocation();
            }
        }
        return this;
    }

    private void updateText() {
        hologram.setText(hologramMark.getTextAsComponent());

        float scaleX = HologramUtils.calculateScale(spaceWidth, this.barWidth);
        float scaleY = HologramUtils.calculateScale(HologramUtils.LINE_HEIGHT, this.barHeight);
        this.setScale(scaleX, scaleY);

        this.markTextWidth = this.barWidth * this.progress;
        System.out.printf("progress %.2f%% -> %.4f\n", this.progress * 100.0, markTextWidth);
        float spaceScaleX = HologramUtils.calculateScale(spaceWidth, this.markTextWidth);
        markWidth = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        markHeight = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        hologramMark.setScale(spaceScaleX, scaleY, 1.0f);
        if (!hologramMark.isDead()) {
            hologramMark.update();
        }
    }

    @Override
    public void performClick(ClickMeta meta) {
    }

    /**
     * 设置当进度条的进度出现变化时执行的操作
     * @param progressChanged 数值改变事件
     */
    public ProgressBar setOnProgressChanged(@Nullable ValueChangedEvent<ProgressBar, Double> progressChanged) {
        this.progressChanged = progressChanged;
        return this;
    }

    /**
     * 设置当进度条的进度出现变化时执行的操作
     * @param supplier 数值改变事件
     */
    public ProgressBar setOnProgressChanged(@Nullable Supplier<ValueChangedEvent<ProgressBar, Double>> supplier) {
        if (supplier == null) {
            this.progressChanged = null;
        } else {
            this.progressChanged = (oldValue, newValue, element) -> {
                ValueChangedEvent<ProgressBar, Double> progressChanged = supplier.get();
                if (progressChanged != null) {
                    progressChanged.perform(oldValue, newValue, element);
                }
            };
        }
        return this;
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
