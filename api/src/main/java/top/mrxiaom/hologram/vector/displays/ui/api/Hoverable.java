package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

/**
 * 可悬停元素
 */
public interface Hoverable {
    void tryUpdateHoverState(boolean hover);

    static boolean handleHover(Terminal<?> terminal, EntityTextDisplay entity) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location eyeLocation = player.getEyeLocation();
            Location point = HologramUtils.raytraceHologram(terminal, entity, eyeLocation);
            if (point != null && eyeLocation.distance(point) <= terminal.getInteractDistance()) {
                return true;
            }
        }
        return false;
    }
    static boolean handleHover(Terminal<?> terminal, EntityItemDisplay entity) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location eyeLocation = player.getEyeLocation();
            Location point = HologramUtils.raytraceHologram(terminal, entity, eyeLocation);
            if (point != null && eyeLocation.distance(point) <= terminal.getInteractDistance()) {
                return true;
            }
        }
        return false;
    }
}
