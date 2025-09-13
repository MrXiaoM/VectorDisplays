package top.mrxiaom.hologram.vector.displays.ui.widget;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.utils.AdventureHelper;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.Hoverable;
import top.mrxiaom.hologram.vector.displays.ui.event.ClickEvent;
import top.mrxiaom.hologram.vector.displays.ui.event.HoverStateChange;

import java.util.function.Supplier;

public class Button extends Element implements Hoverable {
    private @NotNull String text = "";
    private boolean hoverState = false;
    private ClickEvent<Button> clickEvent;
    private HoverStateChange<Button> hoverStateChange;
    public Button(@NotNull String id) {
        super(id);
    }
    public Button(@NotNull String id, @NotNull String text) {
        this(id);
        setText(text);
    }

    /**
     * 获取悬浮字文本
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * 设置悬浮字文本，并重新计算悬浮字大小
     * @param text 支持 MiniMessage
     */
    public void setText(@NotNull String text) {
        this.text = text;
        this.hologram.setText(AdventureHelper.miniMessage(text));
        this.calculateSize();
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param clickEvent 点击事件
     */
    public void setOnClick(@Nullable ClickEvent<Button> clickEvent) {
        this.clickEvent = clickEvent;
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param supplier 点击事件
     */
    public void setOnClick(@Nullable Supplier<ClickEvent<Button>> supplier) {
        if (supplier == null) {
            this.clickEvent = null;
        } else {
            this.clickEvent = (player, action, element) -> {
                ClickEvent<Button> clickEvent = supplier.get();
                if (clickEvent != null) {
                    clickEvent.perform(player, action, element);
                }
            };
        }
    }

    /**
     * 设置当有任意玩家的准心指向这个元素的状态更新时执行操作
     * @param hoverStateChange 悬停状态变化事件
     */
    public void setOnHoverStateChange(HoverStateChange<Button> hoverStateChange) {
        this.hoverStateChange = hoverStateChange;
        hoverStateChange.perform(false, this);
    }

    @Override
    public void performClick(Player player, Action action) {
        if (clickEvent != null) {
            clickEvent.perform(player, action, this);
        }
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
