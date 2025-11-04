package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ClickMeta {
    private final Player player;
    private final Action action;

    protected ClickMeta(Player player, Action action) {
        this.player = player;
        this.action = action;
    }

    public Player getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }
}
