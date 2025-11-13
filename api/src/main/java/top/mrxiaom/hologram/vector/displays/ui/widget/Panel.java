package top.mrxiaom.hologram.vector.displays.ui.widget;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Panel extends TextElement<Panel> {
    private double panelWidth, panelHeight;
    /**
     * 主要缩放，用于通过 panelWidth 和 panelHeight 来设置面板的基本大小
     */
    private final float[] mainScale = new float[] { 1.0f, 1.0f, 1.0f };
    /**
     * 额外缩放，用于给用户使用 setScale 等函数设置面板的缩放大小
     */
    private final float[] extraScale = new float[] { 1.0f, 1.0f, 1.0f };
    double spaceWidth;
    private Double interactDistance = null;
    private final List<Element<?, ?>> elements = new ArrayList<>();
    public Panel(@NotNull String id, double width, double height) {
        super(id);
        this.getEntity().setText(Component.text("                "));
        this.spaceWidth = HologramFont.getTextRenderer().getWidth(getEntity().getTextAsComponent());
        this.setPanelSize(width, height);
    }

    @Override
    public void init() {
        super.init();
        for (Element<?, ?> element : elements) {
            element.init();
        }
    }

    @Override
    public void dispose() {
        for (Element<?, ?> element : elements) {
            element.dispose();
        }
        super.dispose();
    }

    @Override
    public void onTimerTick() {
        super.onTimerTick();
        for (Element<?, ?> element : elements) {
            if (element.isEnabled()) {
                element.onTimerTick();
            }
        }
    }

    @Override
    public void updateLocation() {
        super.updateLocation();
        for (Element<?, ?> element : elements) {
            element.updateLocation();
        }
    }

    /**
     * 预执行点击操作，将事件传递给所有子 Element
     * @param player 玩家
     * @param action 操作类型
     * @param eyeLocation <code>player.getEyeLocation()</code>
     */
    @Override
    public boolean beforePerformClick(Player player, Action action, Location eyeLocation) {
        float[] rotation = getRotation();
        double interactDistance = this.interactDistance == null
                ? getTerminal().getInteractDistance()
                : this.interactDistance;

        return HologramUtils.commonPerformClick(player, action, eyeLocation, elements, rotation, interactDistance);
    }

    /**
     * 获取元素交互距离（单位: 方块），只有玩家的头部与元素之间小于这个距离时，才能悬停并点击
     * <p>
     * 如果返回 <code>null</code>，则代表使用 Terminal 中的值
     */
    @Nullable
    public Double getInteractDistance() {
        return interactDistance;
    }

    /**
     * 设置元素交互距离（单位: 方块），只有玩家的头部与元素之间小于这个距离时，才能悬停并点击
     * <p>
     * 如果设置为 <code>null</code>，则代表使用 Terminal 中的值
     */
    public Panel setInteractDistance(@Nullable Double interactDistance) {
        this.interactDistance = interactDistance;
        return this;
    }

    /**
     * 获取面板宽度
     */
    public double getPanelWidth() {
        return panelWidth;
    }

    /**
     * 获取面板高度
     */
    public double getPanelHeight() {
        return panelHeight;
    }

    /**
     * 设置面板大小，使用文字参考系
     * @param width 宽度
     * @param height 高度
     */
    public void setPanelSize(double width, double height) {
        this.panelWidth = width;
        this.panelHeight = height;
        float scaleX = HologramUtils.calculateScale(spaceWidth, this.panelWidth);
        float scaleY = HologramUtils.calculateScale(HologramUtils.LINE_HEIGHT, this.panelHeight);
        this.mainScale[0] = scaleX;
        this.mainScale[1] = scaleY;
        this.mergeScale();
    }

    /**
     * 将该 Panel 绑定到一个 Terminal，请务必在 <code>addElement</code> 之前完成绑定
     */
    public Panel bind(Terminal<?> terminal) {
        terminal.bindElement(this);
        return this;
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param element 元素实例
     */
    public void addElement(Element<?, ?> element) {
        Terminal<?> terminal = terminal();
        if (terminal == null) {
            throw new IllegalStateException("在执行 panel.addElement(element); 之前，应该先执行 panel.bind(terminal);");
        }
        terminal.bindElement(element);
        element.setParent(this);
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

    public void clearElements() {
        elements.clear();
    }

    /**
     * 用于合并两组缩放数值，应用到悬浮字中
     */
    private void mergeScale() {
        this.scaleX = this.mainScale[0] * this.extraScale[0];
        this.scaleY = this.mainScale[1] * this.extraScale[1];
        float scaleZ = this.mainScale[2] * this.extraScale[2];
        this.getEntity().setScale(scaleX, scaleY, scaleZ);
    }

    @Override
    public Panel setScale(float scaleX, float scaleY) {
        this.extraScale[0] = scaleX;
        this.extraScale[1] = scaleY;
        this.mergeScale();
        return this;
    }

    @Override
    public Panel setScale(float scale) {
        this.extraScale[0] = scale;
        this.extraScale[1] = scale;
        this.extraScale[2] = scale;
        this.mergeScale();
        return this;
    }

    @Override
    public Panel setScaleX(float scaleX) {
        this.extraScale[0] = scaleX;
        this.mergeScale();
        return this;
    }

    @Override
    public Panel setScaleY(float scaleY) {
        this.extraScale[1] = scaleY;
        this.mergeScale();
        return this;
    }

    @Override
    public float getScaleX() {
        return this.extraScale[0];
    }

    @Override
    public float getScaleY() {
        return this.extraScale[1];
    }
}
