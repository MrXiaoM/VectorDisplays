package top.mrxiaom.hologram.vector.displays.ui.event;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.AbstractEntity;
import top.mrxiaom.hologram.vector.displays.hologram.EntityDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.ItemElement;
import top.mrxiaom.hologram.vector.displays.ui.widget.Triangle;

/**
 * 元素悬停状态变更事件
 */
@FunctionalInterface
public interface HoverStateChange<E extends Element<E, ?>> {
    void perform(boolean newState, E element);

    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(int hover, int normal) {
        return hoverBg(hover, normal, HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(int hover, int normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            if (element.getEntity() instanceof EntityTextDisplay txt) {
                txt.setBackgroundColor(newState ? hover : normal);
            }
            if (element instanceof Triangle triangle) {
                triangle.getStyle().setBackgroundColor(newState ? hover : normal);
            }
            if (action != null) action.perform(newState, element);
        };
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(Material hover, Material normal) {
        return hoverBg(new ItemStack(hover), new ItemStack(normal));
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(Material hover, Material normal, @Nullable HoverStateChange<E> action) {
        return hoverBg(new ItemStack(hover), new ItemStack(normal), action);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(ItemStack hover, ItemStack normal) {
        return hoverBg(hover, normal, HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverBg(ItemStack hover, ItemStack normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            if (element.getEntity() instanceof EntityItemDisplay txt) {
                txt.setItemStack(newState ? hover : normal);
            }
            if (action != null) action.perform(newState, element);
        };
    }

    static <E extends Element<E, ?>> HoverStateChange<E> hoverScale(float hover) {
        return hoverScale(hover, HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverScale(float hover, float normal) {
        return hoverScale(hover, normal, HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverScale(float hover, @Nullable HoverStateChange<E> action) {
        return hoverScale(hover, 1.0f, action);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverScale(float hover, float normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            AbstractEntity<?> entity = element.getEntity();
            if (entity instanceof EntityDisplay<?> display) {
                float scale = newState ? hover : normal;
                float scaleX = element.getScaleX();
                float scaleY = element.getScaleY();
                float scaleZ;
                if (element instanceof ItemElement<?> item) {
                    scaleZ = item.getScaleZ();
                } else {
                    scaleZ = 1.0f;
                }
                display.setScale(scaleX * scale, scaleY * scale, scaleZ * scale);
            }
            if (action != null) action.perform(newState, element);
        };
    }

    @SafeVarargs
    static <E extends Element<E, ?>> HoverStateChange<E> of(HoverStateChange<E>... events) {
        return (newState, element) -> {
            for (HoverStateChange<E> event : events) {
                event.perform(newState, element);
            }
        };
    }

    static <E extends Element<E, ?>> HoverStateChange<E> updateEntity() {
        return (newState, element) -> element.update();
    }

    static <E extends Element<E, ?>> HoverStateChange<E> hoverGlow() {
        return hoverGlow(HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverGlow(boolean reverse) {
        return hoverGlow(reverse, HoverStateChange.<E>updateEntity());
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverGlow(@Nullable HoverStateChange<E> action) {
        return hoverGlow(false, action);
    }
    static <E extends Element<E, ?>> HoverStateChange<E> hoverGlow(boolean reverse, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            AbstractEntity<?> entity = element.getEntity();
            entity.setGlowing(newState != reverse);
            if (action != null) action.perform(newState, element);
        };
    }
}
