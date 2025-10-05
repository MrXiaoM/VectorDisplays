package top.mrxiaom.hologram.vector.displays.ui.event;

import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;

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
}
