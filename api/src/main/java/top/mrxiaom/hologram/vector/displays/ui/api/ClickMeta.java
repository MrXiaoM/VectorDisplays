package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import top.mrxiaom.hologram.vector.displays.utils.Point2D;

/**
 * 点击事件的元数据
 */
public class ClickMeta {
    private final Player player;
    private final Action action;
    private final Point2D whereClicked;

    protected ClickMeta(Player player, Action action, Point2D whereClicked) {
        this.player = player;
        this.action = action;
        this.whereClicked = whereClicked;
    }

    /**
     * 获取进行点击操作的玩家
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 获取玩家点击方式
     */
    public Action getAction() {
        return action;
    }

    /**
     * 获取玩家点击元素的位置，在终端平面上的相对坐标投影
     * <p>
     * 以元素的实体坐标为原点，x方向向右，y方向向上
     */
    public Point2D getWhereClicked() {
        return whereClicked;
    }
}
