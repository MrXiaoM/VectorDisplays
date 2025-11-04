package top.mrxiaom.hologram.vector.displays.ui.event;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;

/**
 * 元素点击事件，更推荐使用 ElementClickEvent 以获得更全面的参数
 */
@FunctionalInterface
public interface ClickEvent<E extends Element<E, ?>> {
    void perform(Player player, Action action, E element);

    static <E extends Element<E, ?>> ClickEvent<E> turnPage(String pageName) {
        return (player, action, element) -> element.getTerminal().turnPage(pageName);
    }
}
