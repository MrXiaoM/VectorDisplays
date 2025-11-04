package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.AbstractEntity;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.ui.EnumAlign;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.event.TimerTickEvent;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

/**
 * 悬浮字界面元素
 */
public abstract class Element<This extends Element<This, Entity>, Entity extends AbstractEntity<Entity>> {
    private final Calculator calculator = new Calculator(this);
    private final @NotNull String id;
    private Terminal<?> terminal;
    private EnumAlign align = EnumAlign.CENTER;
    private double x, y, zIndex = 1;
    protected float scaleX = 1.0f, scaleY = 1.0f;
    protected double width, height;
    private TimerTickEvent<This> timerTickEvent;
    protected final @NotNull Entity hologram;
    private float @Nullable [] additionalRotation = null;
    public Element(@NotNull String id) {
        this.id = id;
        this.hologram = createHologram();
    }

    public Calculator calc() {
        return calculator;
    }

    protected abstract Entity createHologram();

    @NotNull
    public Entity getEntity() {
        return hologram;
    }

    @SuppressWarnings({"unchecked"})
    public This $this() {
        return (This) this;
    }

    protected void setTerminal(@NotNull Terminal<?> terminal) {
        this.terminal = terminal;
        this.getEntity().setParent(terminal.getHologram());
    }

    /**
     * 获取悬浮字所在的终端面板
     */
    @NotNull
    public Terminal<?> getTerminal() {
        return terminal;
    }


    /**
     * 界面元素的ID
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * 获取元素在X方向上的缩放大小
     */
    public float getScaleX() {
        return scaleX;
    }

    /**
     * 获取元素在Y方向上的缩放大小
     */
    public float getScaleY() {
        return scaleY;
    }

    /**
     * 设置元素在X方向上的缩放大小
     */
    public This setScaleX(float scaleX) {
        return setScale(scaleX, scaleY);
    }

    /**
     * 设置元素在Y方向上的缩放大小
     */
    public This setScaleY(float scaleY) {
        return setScale(scaleX, scaleY);
    }

    /**
     * 设置元素缩放大小
     */
    public This setScale(float scale) {
        return setScale(scale, scale);
    }

    /**
     * 设置元素缩放大小
     */
    public This setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        return $this();
    }

    /**
     * 获取元素的额外旋转量 (四元数)
     * @return <code>null</code> 代表不进行额外旋转
     */
    public float @Nullable [] getAdditionalRotation() {
        return additionalRotation;
    }

    /**
     * 设置元素的额外旋转量 (四元数)
     * @param additionalRotation <code>null</code> 代表不进行额外旋转
     */
    public This setAdditionalRotation(float @Nullable [] additionalRotation) {
        this.additionalRotation = additionalRotation;
        return $this();
    }

    /**
     * 设置元素的额外旋转量 (欧拉角)
     * @param yaw 偏航角
     * @param pitch 俯仰角
     * @param roll 翻滚角
     */
    public This setAdditionalRotation(float yaw, float pitch, float roll) {
        return setAdditionalRotation(QuaternionUtils.fromEulerYXZtoQuaternion(yaw, pitch, roll));
    }

    /**
     * 设置元素的额外旋转量 (欧拉角)
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    public This setAdditionalRotation(float yaw, float pitch) {
        return setAdditionalRotation(yaw, pitch, 0.0f);
    }

    /**
     * 设置元素的额外旋转量 (欧拉角)
     * @param roll 翻滚角
     */
    public This setAdditionalRotation(float roll) {
        return setAdditionalRotation(0.0f, 0.0f, roll);
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
    public abstract void calculateSize();

    /**
     * 界面元素初始化方法，在这里确定悬浮字位置，并生成悬浮字
     */
    public void init() {
        if (terminal != null) hologram.setRenderMode(terminal.getRenderMode());
        calculateSize();
        HologramAPI.getHologram().spawn(hologram, decideLocation());
    }

    public float[] getRotation() {
        if (additionalRotation == null) {
            return terminal.getRotation();
        }
        return QuaternionUtils.multiplyF(terminal.getRotation(), additionalRotation);
    }

    /**
     * 提交悬浮字更新
     * @see EntityTextDisplay#update()
     */
    public void update() {
        hologram.update();
    }

    /**
     * 当玩家点击悬浮字时执行的操作
     * @param meta 点击事件传入参数
     */
    public void performClick(ClickMeta meta) {
        performClick(meta.getPlayer(), meta.getAction());
    }

    /**
     * 当玩家点击悬浮字时执行的操作
     * @param player 玩家
     * @param action 点击方式
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public void performClick(Player player, Action action) {
    }

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
        hologram.removeAllViewers();
        HologramAPI.getHologram().remove(hologram);
    }
}
