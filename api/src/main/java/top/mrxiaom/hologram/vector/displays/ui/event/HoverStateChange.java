package top.mrxiaom.hologram.vector.displays.ui.event;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;

/**
 * 元素悬停状态变更事件
 */
@FunctionalInterface
public interface HoverStateChange<E extends Element<E, ?>> {
    void perform(boolean newState, E element);

    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(int hover, int normal) {
        return hoverBg(hover, normal, null);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(int hover, int normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            if (element.getEntity() instanceof EntityTextDisplay txt) {
                txt.setBackgroundColor(newState ? hover : normal);
            }
            if (action != null) action.perform(newState, element);
            element.update();
        };
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(Material hover, Material normal) {
        return hoverBg(new ItemStack(hover), new ItemStack(normal));
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(Material hover, Material normal, @Nullable HoverStateChange<E> action) {
        return hoverBg(new ItemStack(hover), new ItemStack(normal), action);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(ItemStack hover, ItemStack normal) {
        return hoverBg(hover, normal, null);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(ItemStack hover, ItemStack normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            if (element.getEntity() instanceof EntityItemDisplay txt) {
                txt.setItemStack(newState ? hover : normal);
            }
            if (action != null) action.perform(newState, element);
            element.update();
        };
    }
}
