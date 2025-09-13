package top.mrxiaom.hologram.vector.displays.ui.event;

import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;

/**
 * 元素悬停状态变更事件
 */
@FunctionalInterface
public interface HoverStateChange<E extends Element> {
    void perform(boolean newState, E element);

    static <E extends Element> HoverStateChange<E> hoverBg(int hover, int normal) {
        return hoverBg(hover, normal, null);
    }
    static <E extends Element> HoverStateChange<E> hoverBg(int hover, int normal, @Nullable HoverStateChange<E> action) {
        return (newState, element) -> {
            element.setBackgroundColor(newState ? hover : normal);
            if (action != null) action.perform(newState, element);
            element.update();
        };
    }
}
