package top.mrxiaom.hologram.vector.displays.ui.api;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.hologram.TextHologram;
import top.mrxiaom.hologram.vector.displays.ui.EnumAlign;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.event.TimerTickEvent;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;


/**
 * 悬浮字界面元素
 */
public abstract class Element<This extends Element<This>> implements HologramWrapper<This> {
    private final @NotNull String id;
    private Terminal<?> terminal;
    private EnumAlign align;
    private double x, y, zIndex;
    private float scale = 1.0f;
    private double width, height;
    private double textWidth, textHeight;
    private TimerTickEvent<This> timerTickEvent;
    protected final @NotNull TextHologram hologram;
    public Element(@NotNull String id) {
        this.id = id;
        this.hologram = new TextHologram(RenderMode.VIEWER_LIST)
                .setInterpolationDurationTransformation(3)
                .setInterpolationDurationRotation(0)
                .setAlignment(TextDisplay.TextAlignment.LEFT)
                .setBillboard(Display.Billboard.FIXED)
                .setText(Component.text(""))
                .removeAllViewers();
    }

    @SuppressWarnings({"unchecked"})
    public This $this() {
        return (This) this;
    }

    void setTerminal(@NotNull Terminal<?> terminal) {
        this.terminal = terminal;
        this.hologram.setRightRotation(terminal.getRotation());
    }

    /**
     * 获取悬浮字所在的终端面板
     */
    @NotNull
    public Terminal<?> getTerminal() {
        return terminal;
    }

    /**
     * 获取悬浮字实例 (尽量不要使用这个方法)
     */
    @NotNull
    @Override
    public TextHologram getHologram() {
        return hologram;
    }

    /**
     * 界面元素的ID
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * 获取元素缩放大小
     */
    public float getScale() {
        return scale;
    }

    /**
     * 设置元素缩放大小
     */
    public This setScale(float scale) {
        this.scale = scale;
        this.hologram.setScale(scale, scale, scale);
        return $this();
    }

    /**
     * 获取元素位置对齐方式
     */
    public EnumAlign getAlign() {
        return align;
    }

    /**
     * 设置元素位置对齐方式
     */
    public This setAlign(EnumAlign align) {
        this.align = align;
        return $this();
    }

    /**
     * 获取元素坐标
     */
    public double getX() {
        return x;
    }

    /**
     * 获取元素坐标
     */
    public double getY() {
        return y;
    }

    public double getZIndex() {
        return zIndex;
    }

    /**
     * 设置元素坐标。坐标并不实时更新，需要执行 <code>updateLocation()</code> 提交悬浮字位置更新
     * @see Element#updateLocation()
     */
    public This setX(double x) {
        this.x = x;
        return $this();
    }

    /**
     * 设置元素坐标。坐标并不实时更新，需要执行 <code>updateLocation()</code> 提交悬浮字位置更新
     * @see Element#updateLocation()
     */
    public This setY(double y) {
        this.y = y;
        return $this();
    }

    public This setZIndex(double zIndex) {
        this.zIndex = zIndex;
        return $this();
    }

    /**
     * 设置元素坐标。坐标并不实时更新，需要执行 <code>updateLocation()</code> 提交悬浮字位置更新
     * @see Element#updateLocation()
     */
    public This setPos(double x, double y) {
        this.x = x;
        this.y = y;
        return $this();
    }

    /**
     * 获取元素在世界坐标系上的宽度
     */
    public double getWidth() {
        return width;
    }

    /**
     * 获取元素在世界坐标系上的高度
     */
    public double getHeight() {
        return height;
    }

    /**
     * 获取该元素的悬浮字宽度
     */
    public double getTextWidth() {
        return textWidth;
    }

    /**
     * 获取该元素的悬浮字高度
     */
    public double getTextHeight() {
        return textHeight;
    }

    /**
     * 将悬浮字传送到 <code>decideLocation()</code> 的位置
     * @see Element#decideLocation()
     */
    public void updateLocation() {
        hologram.teleport(decideLocation());
    }

    protected double[] decideLocationRaw(double pX, double pY) {
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
        double z = rootLocation.getZ() + (0.001 * zIndex);

        // 根据排列方式的不同，计算在世界上的初始坐标
        return align.get(rootX, rootY, z, rootWidth, rootHeight, x, y, width, height);
    }

    /**
     * 根据 terminal 的旋转关系，计算出这个子元素的悬浮字应当出现在什么坐标处
     */
    protected Location decideLocation() {
        double[] raw = decideLocationRaw(x, y);
        // 根据终端旋转量进行坐标变换
        return terminal.getRotatedLoc(raw);
    }

    /**
     * 根据悬浮字的文本，计算悬浮字在世界上的长宽尺寸
     */
    public void calculateSize() {
        this.textWidth = HologramFont.getWidth(hologram.getTextAsComponent()) * scale;
        this.textHeight = HologramUtils.getLines(hologram) * HologramUtils.LINE_HEIGHT * scale;
        this.width = textWidth * HologramFont.getCharScale();
        this.height = textHeight * HologramFont.getCharScale();
    }

    /**
     * 界面元素初始化方法，在这里确定悬浮字位置，并生成悬浮字
     */
    public void init() {
        hologram.setRightRotation(getRotation());
        calculateSize();
        HologramAPI.getHologram().spawn(hologram, decideLocation());
    }

    public float[] getRotation() {
        return terminal.getRotation();
    }

    /**
     * 提交悬浮字更新
     * @see TextHologram#update()
     */
    public void update() {
        hologram.update();
    }

    /**
     * 当玩家点击悬浮字时执行的操作
     * @param player 玩家
     * @param action 点击方式
     */
    public abstract void performClick(Player player, Action action);

    /**
     * 当定时器遍历悬浮字时执行的操作
     */
    public void onTimerTick() {
        if (timerTickEvent != null) {
            timerTickEvent.tick($this());
        }
    }

    /**
     * 设置当定时器遍历悬浮字时执行的操作
     * @param timerTickEvent 执行操作
     */
    public This setTimerTickEvent(@Nullable TimerTickEvent<This> timerTickEvent) {
        this.timerTickEvent = timerTickEvent;
        return $this();
    }

    /**
     * 销毁悬浮字
     */
    public void dispose() {
        HologramAPI.getHologram().remove(hologram);
        hologram.removeAllViewers();
    }
}
