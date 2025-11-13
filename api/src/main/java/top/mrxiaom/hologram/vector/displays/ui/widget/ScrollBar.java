package top.mrxiaom.hologram.vector.displays.ui.widget;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.AbstractEntity;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.ClickMeta;
import top.mrxiaom.hologram.vector.displays.ui.api.Hoverable;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.ui.event.HoverStateChange;
import top.mrxiaom.hologram.vector.displays.ui.event.ValueChangedEvent;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 滚动条控件
 */
public class ScrollBar extends TextElement<ScrollBar> implements EntityTextDisplayWrapper<ScrollBar>, Hoverable {
    private int division;
    private double progress;
    private int foregroundColor;
    private float barWidth, barHeight;
    private final EntityTextDisplay hologramMark;
    private float markTextWidth;
    private double markWidth, markHeight;
    private final float spaceWidth;
    private boolean hoverState = false;
    private ValueChangedEvent<ScrollBar, Double> progressChanged;
    private HoverStateChange<ScrollBar> hoverStateChange;

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
     * @param division 分为多少块（确定滑动按钮大小）
     * @param barWidth 滚动条宽度
     * @param barHeight 滚动条高度
     */
    public ScrollBar(@NotNull String id, int division, float barWidth, float barHeight) {
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
        this.setBarSize(barWidth, barHeight);
    }

    @Override
    public @NotNull Collection<AbstractEntity<?>> collectEntities() {
        return Collections.unmodifiableList(Lists.newArrayList(hologram, hologramMark));
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
        double[] loc = ar != null
                ? QuaternionUtils.rotateChildrenToDouble(decideLocationRaw(getX(), getY()), ar, raw)
                : raw;
        return getTerminal().getRotatedLoc(loc);
    }

    /**
     * 获取滚动条的宽度
     */
    public float getBarWidth() {
        return barWidth;
    }

    /**
     * 获取滚动条的高度
     */
    public float getBarHeight() {
        return barHeight;
    }

    /**
     * 设置滚动条的大小，使用文字参考系
     * @param barWidth 滚动条宽度
     * @param barHeight 滚动条高度
     */
    public ScrollBar setBarSize(float barWidth, float barHeight) {
        this.barWidth = barWidth;
        this.barHeight = barHeight;
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
    public ScrollBar setProgress(double progress) {
        double newProgress = Math.max(0.0, Math.min(1.0, progress));
        if (newProgress != this.progress) {
            double oldProgress = this.progress;
            this.progress = newProgress;
            if (this.progressChanged != null) {
                this.progressChanged.perform(oldProgress, newProgress, this);
            }
            this.updateText();
            if (!getEntity().isDead()) {
                this.updateLocation();
            }
        }
        return this;
    }

    private void updateText() {
        getEntity().setText(hologramMark.getTextAsComponent());

        float scaleX = HologramUtils.calculateScale(spaceWidth, this.barWidth);
        float scaleY = HologramUtils.calculateScale(HologramUtils.LINE_HEIGHT, this.barHeight);
        this.setScale(scaleX, scaleY);

        this.markTextWidth = this.barWidth / this.division;
        float spaceScaleX = HologramUtils.calculateScale(spaceWidth, this.markTextWidth);
        markWidth = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        markHeight = HologramUtils.LINE_HEIGHT * HologramFont.getCharScale() * scaleY;
        hologramMark.setScale(spaceScaleX, scaleY, 1.0f);
    }

    @Override
    public void performClick(ClickMeta meta) {
        setProgress((meta.getWhereClicked().getX() + (getTextWidth() / 2.0)) / getTextWidth());
    }

    /**
     * 设置当滚动条的进度出现变化时执行的操作
     * @param progressChanged 数值改变事件
     */
    public ScrollBar setOnProgressChanged(@Nullable ValueChangedEvent<ScrollBar, Double> progressChanged) {
        this.progressChanged = progressChanged;
        return this;
    }

    /**
     * 设置当滚动条的进度出现变化时执行的操作
     * @param supplier 数值改变事件
     */
    public ScrollBar setOnProgressChanged(@Nullable Supplier<ValueChangedEvent<ScrollBar, Double>> supplier) {
        if (supplier == null) {
            this.progressChanged = null;
        } else {
            this.progressChanged = (oldValue, newValue, element) -> {
                ValueChangedEvent<ScrollBar, Double> progressChanged = supplier.get();
                if (progressChanged != null) {
                    progressChanged.perform(oldValue, newValue, element);
                }
            };
        }
        return this;
    }

    @NotNull
    public EntityTextDisplay getHologramMark() {
        return hologramMark;
    }

    public ScrollBar configureHologramMark(Consumer<EntityTextDisplay> consumer) {
        consumer.accept(hologramMark);
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

    /**
     * 设置当有任意玩家的准心指向这个元素的状态更新时执行操作
     * @param hoverStateChange 悬停状态变化事件
     */
    public ScrollBar setOnHoverStateChange(HoverStateChange<ScrollBar> hoverStateChange) {
        this.hoverStateChange = hoverStateChange;
        hoverStateChange.perform(hoverState, this);
        return this;
    }

    @Override
    public void onTimerTick() {
        super.onTimerTick();

        tryUpdateHoverState(Hoverable.handleHover(getTerminal(), getAdditionalRotation(), this));
    }

    @Override
    public void tryUpdateHoverState(boolean hover) {
        if (hover == hoverState) return;
        hoverState = hover;
        if (hoverStateChange != null) {
            hoverStateChange.perform(hover, this);
        }
    }
}
