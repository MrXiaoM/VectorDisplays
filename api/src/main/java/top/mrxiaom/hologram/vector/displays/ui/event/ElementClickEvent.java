package top.mrxiaom.hologram.vector.displays.ui.event;

import top.mrxiaom.hologram.vector.displays.ui.api.ClickMeta;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;

/**
 * 元素点击事件
 */
@FunctionalInterface
public interface ElementClickEvent<E extends Element<E, ?>> {
    void perform(ClickMeta meta, E element);

    static <E extends Element<E, ?>> ElementClickEvent<E> turnPage(String pageName) {
        return (meta, element) -> element.getTerminal().turnPage(pageName);
    }
}
