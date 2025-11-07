package top.mrxiaom.hologram.vector.displays.ui.widget;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.ui.api.ClickMeta;
import top.mrxiaom.hologram.vector.displays.ui.api.Hoverable;
import top.mrxiaom.hologram.vector.displays.ui.api.ItemElement;
import top.mrxiaom.hologram.vector.displays.ui.event.ClickEvent;
import top.mrxiaom.hologram.vector.displays.ui.event.ElementClickEvent;
import top.mrxiaom.hologram.vector.displays.ui.event.HoverStateChange;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

import java.util.function.Supplier;

public class Item extends ItemElement<Item> implements Hoverable {
    private boolean hoverState = false;
    private ClickEvent<Item> oldClickEvent;
    private ElementClickEvent<Item> clickEvent;
    private HoverStateChange<Item> hoverStateChange;
    public Item(@NotNull String id) {
        this(id, null);
        this.getEntity().setRightRotation(QuaternionUtils.fromEulerYXZtoQuaternion(180.0f, 0.0f, 0.0f));
    }
    public Item(@NotNull String id, @Nullable ItemStack itemStack) {
        super(id);
        setItemStack(itemStack);
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param clickEvent 点击事件
     */
    @Deprecated
    public Item setOnClick(@Nullable ClickEvent<Item> clickEvent) {
        this.oldClickEvent = clickEvent;
        return this;
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param clickEvent 点击事件
     */
    public Item setOnClick(@Nullable ElementClickEvent<Item> clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param clickEvent 点击事件
     */
    public Item setOnElementClick(@Nullable ElementClickEvent<Item> clickEvent) {
        return setOnClick(clickEvent);
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param supplier 点击事件
     */
    @Deprecated
    public Item setOnClick(@Nullable Supplier<ClickEvent<Item>> supplier) {
        if (supplier == null) {
            this.oldClickEvent = null;
        } else {
            this.oldClickEvent = (player, action, element) -> {
                ClickEvent<Item> clickEvent = supplier.get();
                if (clickEvent != null) {
                    clickEvent.perform(player, action, element);
                }
            };
        }
        return this;
    }

    /**
     * 设置当玩家点击这个元素时执行操作
     * @param supplier 点击事件
     */
    public Item setOnElementClick(@Nullable Supplier<ElementClickEvent<Item>> supplier) {
        if (supplier == null) {
            this.clickEvent = null;
        } else {
            this.clickEvent = (meta, element) -> {
                ElementClickEvent<Item> clickEvent = supplier.get();
                if (clickEvent != null) {
                    clickEvent.perform(meta, element);
                }
            };
        }
        return this;
    }

    /**
     * 设置当有任意玩家的准心指向这个元素的状态更新时执行操作
     * @param hoverStateChange 悬停状态变化事件
     */
    public Item setOnHoverStateChange(HoverStateChange<Item> hoverStateChange) {
        this.hoverStateChange = hoverStateChange;
        hoverStateChange.perform(false, this);
        return this;
    }

    @Override
    public void performClick(ClickMeta meta) {
        if (clickEvent != null) {
            clickEvent.perform(meta, this);
        }
        if (oldClickEvent != null) {
            oldClickEvent.perform(meta.getPlayer(), meta.getAction(), this);
        }
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
