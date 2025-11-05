package top.mrxiaom.hologram.vector.displays.ui.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.*;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * 悬浮字界面终端面板，负责定位与包含元素
 */
public abstract class Terminal<This extends Terminal<This>> implements EntityTextDisplayWrapper<This> {
    private final @NotNull String id;
    private @NotNull Location location;
    private final Map<String, List<Element<?, ?>>> pages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final List<Element<?, ?>> elements = new ArrayList<>();
    private final EntityTextDisplay hologram;
    private double width, height;
    private double interactDistance = 2.5;
    private float[] rotation = { 0, 0, 0, 1 };
    private Consumer<This> actionPreTimerTick, actionPostTimerTick, actionPreDispose, actionPostDispose;
    private Terminal(@NotNull RenderMode renderMode, @NotNull String id, @NotNull Location location) {
        this.id = id;
        this.location = location;
        this.hologram = new EntityTextDisplay(renderMode)
                .setInterpolationDurationTransformation(3)
                .setInterpolationDurationRotation(0)
                .setAlignment(TextDisplay.TextAlignment.LEFT)
                .setBillboard(Display.Billboard.FIXED)
                .setText(Component.text(""))
                .setShadow(false)
                .setBackgroundColor(0x30000000);
    }
    public Terminal(@NotNull String id, @NotNull Location location, int widthSpace, int heightLines) {
        this(RenderMode.VIEWER_LIST, id, location, widthSpace, heightLines);
    }
    public Terminal(@NotNull RenderMode renderMode, @NotNull String id, @NotNull Location location, int widthSpace, int heightLines) {
        this(renderMode, id, location);
        setSize(widthSpace, heightLines);
    }
    public Terminal(@NotNull String id, @NotNull Location location, double width, double height) {
        this(RenderMode.VIEWER_LIST, id, location, width, height);
    }
    public Terminal(@NotNull RenderMode renderMode, @NotNull String id, @NotNull Location location, double width, double height) {
        this(renderMode, id, location);
        setSize(width, height);
    }

    /**
     * 获取悬浮字的渲染模式
     */
    @NotNull
    public RenderMode getRenderMode() {
        return hologram.getRenderMode();
    }

    /**
     * 获取面板的靠近渲染距离
     */
    public double getNearbyDistance() {
        return hologram.getNearbyEntityScanningDistance();
    }

    /**
     * 设置玩家靠近面板多少格以内才渲染。注意，这个选项仅在 RenderMode 为 <code>NEARBY</code> 时有效。
     */
    public This setNearbyDistance(double nearDistance) {
        hologram.setNearbyEntityScanningDistance(nearDistance);
        return $this();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public This $this() {
        return (This) this;
    }

    /**
     * 界面元素初始化方法，在这里确定悬浮字位置，并生成悬浮字
     */
    public void init() {
        for (Element<?, ?> element : elements) {
            element.init();
        }
    }

    public This setOnPreDispose(Runnable actionPreDispose) {
        return setOnPreDispose(t -> actionPreDispose.run());
    }
    public This setOnPreDispose(Consumer<This> actionPreDispose) {
        this.actionPreDispose = actionPreDispose;
        return $this();
    }

    public This setOnPostDispose(Runnable actionPostDispose) {
        return setOnPostDispose(t -> actionPostDispose.run());
    }
    public This setOnPostDispose(Consumer<This> actionPostDispose) {
        this.actionPostDispose = actionPostDispose;
        return $this();
    }

    /**
     * 销毁悬浮字
     */
    public void dispose() {
        if (actionPreDispose != null) {
            actionPreDispose.accept($this());
        }
        for (Element<?, ?> element : elements) {
            element.dispose();
        }
        hologram.removeAllViewers();
        HologramAPI.getHologram().remove(hologram);
        if (actionPostDispose != null) {
            actionPostDispose.accept($this());
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    /**
     * 获取悬浮字实例 (尽量不要使用这个方法)
     */
    @NotNull
    @Override
    public EntityTextDisplay getHologram() {
        return hologram;
    }

    public void bindElement(@NotNull Element<?, ?> element) {
        element.setTerminal(this);
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param element 元素实例
     */
    public void addElement(Element<?, ?> element) {
        bindElement(element);
        elements.add(element);
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param element 界面元素
     * @param consumer 额外参数
     */
    public <T extends Element<?, ?>> void addElement(T element, Consumer<T> consumer) {
        consumer.accept(element);
        addElement(element);
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param elements 元素实例
     */
    public void addElements(Element<?, ?>... elements) {
        for (Element<?, ?> element : elements) {
            addElement(element);
        }
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param elements 元素实例
     */
    public void addElements(Collection<Element<?, ?>> elements) {
        if (elements == null) return;
        for (Element<?, ?> element : elements) {
            addElement(element);
        }
    }

    /**
     * 从界面中删除元素
     * @param id 元素ID
     * @return 被删除的元素实例
     */
    @Nullable
    public Element<?, ?> removeElement(String id) {
        for (Object o : elements.toArray()) {
            Element<?, ?> element = (Element<?, ?>) o;
            if (element.getId().equals(id) && removeElement(element)) {
                return element;
            }
        }
        return null;
    }

    /**
     * 从界面中删除元素
     * @param element 元素实例
     * @return 是否删除成功
     */
    public boolean removeElement(Element<?, ?> element) {
        if (elements.remove(element)) {
            element.dispose();
            return true;
        }
        return false;
    }

    /**
     * 获取已添加的元素只读列表
     */
    public List<Element<?, ?>> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Nullable
    public Element<?, ?> getElement(String id) {
        for (Element<?, ?> element : elements) {
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
    }

    @Nullable
    public List<Element<?, ?>> getPage(@NotNull String pageName) {
        return pages.get(pageName);
    }

    public void clearElements() {
        elements.clear();
    }

    public void clearPages() {
        pages.clear();
    }

    public void addPage(@NotNull String pageName) {
        addPage(pageName, elements);
        clearElements();
    }

    public void addPage(@NotNull String pageName, @NotNull List<Element<?, ?>> elements) {
        pages.put(pageName, elements);
    }

    public void addPage(@NotNull String pageName, @NotNull Consumer<PageBuilder> consumer) {
        PageBuilder builder = new PageBuilder(this);
        consumer.accept(builder);
        pages.put(pageName, builder.getElements());
    }

    public void removePage(@NotNull String pageName) {
        pages.remove(pageName);
    }

    public void applyPage(@NotNull String pageName) {
        List<Element<?, ?>> list = pages.get(pageName);
        for (Element<?, ?> element : elements) {
            element.dispose();
        }
        clearElements();
        addElements(list);
    }

    public void turnPage(@NotNull String pageName) {
        applyPage(pageName);
        ensureViewersAdded();
        init();
    }

    /**
     * 获取可以看见这个终端面板的玩家列表
     */
    public List<Player> getViewers() {
        return Collections.unmodifiableList(hologram.getViewers());
    }

    /**
     * 使得玩家不可看见并操作这个终端面板
     * @param player 玩家
     */
    public void removeViewer(Player player) {
        hologram.removeViewer(player);
        for (Element<?, ?> element : elements) {
            element.getEntity().setParent(hologram);
        }
    }

    /**
     * 使得玩家可以看见并操作这个终端面板
     * @param player 玩家
     */
    public void addViewer(Player player) {
        if (!hologram.getViewers().contains(player)) {
            hologram.addViewer(player);
        }
        for (Element<?, ?> element : elements) {
            element.getEntity().setParent(hologram);
        }
    }

    public void ensureViewersAdded() {
        for (Element<?, ?> element : elements) {
            AbstractEntity<?> hologram = element.getEntity();
            for (Player player : getViewers()) {
                if (!hologram.getViewers().contains(player)) {
                    hologram.addViewer(player);
                }
            }
        }
    }

    @NotNull
    public World getWorld() {
        return Objects.requireNonNull(location.getWorld());
    }

    /**
     * 获取悬浮字位置
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * 设置悬浮字位置
     */
    public void setLocation(@NotNull Location location) {
        this.location = location;
        if (!hologram.isDead()) {
            hologram.teleport(location);
            for (Element<?, ?> element : elements) {
                element.updateLocation();
                element.update();
            }
        }
    }

    /**
     * 设置悬浮字的旋转角度，以欧拉角形式。<br>
     * 这个函数会将欧拉角 (YXZ 顺序) 转换为四元数，并应用到悬浮字的 <code>left_rotation</code>
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    public void setRotation(float yaw, float pitch) {
        setRotation(yaw, pitch, 0.0f);
    }

    /**
     * 设置悬浮字的旋转角度，以欧拉角形式。<br>
     * 这个函数会将欧拉角 (YXZ 顺序) 转换为四元数，并应用到悬浮字的 <code>left_rotation</code>
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    public void setRotation(float yaw, float pitch, float roll) {
        setRotation(QuaternionUtils.fromEulerYXZtoQuaternion(yaw, pitch, roll));
    }

    /**
     * 设置悬浮字的旋转角度，以四元数形式。<br>
     * 这会应用到悬浮字的 <code>left_rotation</code>
     */
    public void setRotation(float x, float y, float z, float w) {
        setRotation(new float[] { x, y, z, w });
    }

    /**
     * 设置悬浮字的旋转角度，以四元数形式。<br>
     * 这会应用到悬浮字的 <code>left_rotation</code>
     */
    public void setRotation(float[] rotation) {
        hologram.setLeftRotation(this.rotation = rotation);
    }

    /**
     * 获取悬浮字的旋转角度，以四元数形式
     */
    public float[] getRotation() {
        return rotation;
    }

    /**
     * 获取悬浮字的旋转角度，以欧拉角形式，获取俯仰角
     * @see org.joml.Quaternionf#getEulerAnglesYXZ(org.joml.Vector3f)
     */
    public float getRotationPitch() {
        float x = rotation[0], y = rotation[1], z = rotation[2], w = rotation[3];
        return org.joml.Math.safeAsin(-2.0F * (y * z - w * x));
    }

    /**
     * 获取悬浮字的旋转角度，以欧拉角形式，获取偏航角
     * @see org.joml.Quaternionf#getEulerAnglesYXZ(org.joml.Vector3f)
     */
    public float getRotationYaw() {
        float x = rotation[0], y = rotation[1], z = rotation[2], w = rotation[3];
        return org.joml.Math.atan2(x * z + y * w, 0.5F - y * y - x * x);
    }

    /**
     * 获取悬浮字的旋转角度，以欧拉角形式，获取翻滚角
     * @see org.joml.Quaternionf#getEulerAnglesYXZ(org.joml.Vector3f)
     */
    public float getRotationRoll() {
        float x = rotation[0], y = rotation[1], z = rotation[2], w = rotation[3];
        return org.joml.Math.atan2(y * x + w * z, 0.5F - x * x - z * z);
    }

    /**
     * 获取悬浮字的旋转角度，以欧拉角形式
     * @see org.joml.Quaternionf#getEulerAnglesYXZ(org.joml.Vector3f)
     */
    public float[] getRotationEuler() {
        float x = rotation[0], y = rotation[1], z = rotation[2], w = rotation[3];
        float yaw = org.joml.Math.atan2(x * z + y * w, 0.5F - y * y - x * x);
        float pitch = org.joml.Math.safeAsin(-2.0F * (y * z - w * x));
        float roll = org.joml.Math.atan2(y * x + w * z, 0.5F - x * x - z * z);
        return new float[] { yaw, pitch, roll };
    }

    public Location getRotatedLoc(double[] loc) {
        double[] result = QuaternionUtils.rotateChildrenToDouble(getLocation(), getRotation(), loc);
        return new Location(getWorld(), result[0], result[1], result[2]);
    }

    public float[] getRotated(double[] loc) {
        return QuaternionUtils.rotateChildren(
                HologramUtils.toFloat(location.getX(), location.getY(), location.getZ()),
                getRotation(),
                HologramUtils.toFloat(loc)
        );
    }

    /**
     * 设置终端面板尺寸
     * @param widthSpace 宽度，单位为空格数量。大约一个空格是 27 单位
     * @param heightLines 高度，单位为行数。大约一行是 12 单位
     */
    public void setSize(int widthSpace, int heightLines) {
        String line = " ".repeat(widthSpace);
        StringJoiner joiner = new StringJoiner("\n");
        for (int i = 0; i < heightLines; i++) {
            joiner.add(line);
        }
        hologram.setText(Component.text(joiner.toString()));
        width = HologramUtils.getWidth(hologram);
        height = HologramUtils.getHeight(hologram);
        if (!hologram.isDead()) {
            hologram.update();
            for (Element<?, ?> element : elements) {
                element.updateLocation();
                element.update();
            }
        }
    }

    /**
     * 设置终端面板尺寸
     * <p>
     * 注意，由于计算误差，最终设定的宽度和高度不一定与你传入的数值一致
     * @param width 宽度，单位为像素
     * @param height 高度，单位为像素
     */
    public void setSize(double width, double height) {
        TextComponent component = Component.text("                ");
        hologram.setText(component);
        double oldWidth = HologramFont.getWidth(component);
        double oldHeight = HologramUtils.LINE_HEIGHT;
        float scaleX = HologramUtils.calculateScale(oldWidth, width);
        float scaleY = HologramUtils.calculateScale(oldHeight, height);
        hologram.setScale(scaleX, scaleY, 1.0f);
        this.width = HologramUtils.getWidth(hologram);
        this.height = HologramUtils.getHeight(hologram);
        if (!hologram.isDead()) {
            hologram.update();
            for (Element<?, ?> element : elements) {
                element.updateLocation();
                element.update();
            }
        }
    }

    /**
     * 获取终端面板在世界坐标系上的宽度
     */
    public double getWidth() {
        return width;
    }

    /**
     * 获取终端面板在世界坐标系上的高度
     */
    public double getHeight() {
        return height;
    }

    /**
     * 获取元素交互距离（单位: 方块），只有玩家的头部与元素之间小于这个距离时，才能悬停并点击
     */
    public double getInteractDistance() {
        return interactDistance;
    }

    /**
     * 设置元素交互距离（单位: 方块），只有玩家的头部与元素之间小于这个距离时，才能悬停并点击
     */
    public void setInteractDistance(double interactDistance) {
        this.interactDistance = interactDistance;
    }

    public This setOnPreTimerTick(Runnable actionPreTimerTick) {
        return setOnPreTimerTick(t -> actionPreTimerTick.run());
    }
    public This setOnPreTimerTick(Consumer<This> actionPreTimerTick) {
        this.actionPreTimerTick = actionPreTimerTick;
        return $this();
    }

    public This setOnPostTimerTick(Runnable actionPostTimerTick) {
        return setOnPostTimerTick(t -> actionPostTimerTick.run());
    }
    public This setOnPostTimerTick(Consumer<This> actionPostTimerTick) {
        this.actionPostTimerTick = actionPostTimerTick;
        return $this();
    }

    /**
     * 定时器事件，执行周期由 TerminalManager 决定
     */
    public void onTimerTick() {
        if (actionPreTimerTick != null) {
            actionPreTimerTick.accept($this());
        }
        for (Element<?, ?> element : getElements()) {
            element.onTimerTick();
        }
        if (actionPostTimerTick != null) {
            actionPostTimerTick.accept($this());
        }
    }

    /**
     * 计算元素悬停状态，尝试执行点击操作
     * @param player 玩家
     * @param action 点击方式
     * @return 是否成功执行点击操作
     */
    public boolean tryPerformClick(Player player, Action action) {
        Location eyeLocation = player.getEyeLocation();
        float[] rotation = getRotation();
        double interactDistance = getInteractDistance();
        return HologramUtils.commonPerformClick(player, action, eyeLocation, elements, rotation, interactDistance);
    }
}
