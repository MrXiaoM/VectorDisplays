package top.mrxiaom.hologram.vector.displays.ui.api;

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

    static boolean handleHover(Terminal<?> terminal, float[] additionalRotation, Element<?, ?> element) {
        for (Player player : terminal.getViewers()) {
            Location eyeLocation = HologramUtils.getEyeLocation(player);
            Location point = HologramUtils.raytraceElement(terminal, additionalRotation, element, eyeLocation);
            if (point != null && eyeLocation.distance(point) <= terminal.getInteractDistance()) {
                return true;
            }
        }
        return false;
    }
    @Deprecated
    static boolean handleHover(Terminal<?> terminal, float[] additionalRotation, EntityTextDisplay entity) {
        for (Player player : terminal.getViewers()) {
            Location eyeLocation = HologramUtils.getEyeLocation(player);
            Location point = HologramUtils.raytraceHologram(terminal, additionalRotation, entity, eyeLocation);
            if (point != null && eyeLocation.distance(point) <= terminal.getInteractDistance()) {
                return true;
            }
        }
        return false;
    }
    @Deprecated
    static boolean handleHover(Terminal<?> terminal, float[] additionalRotation, EntityItemDisplay entity) {
        for (Player player : terminal.getViewers()) {
            Location eyeLocation = HologramUtils.getEyeLocation(player);
            Location point = HologramUtils.raytraceHologram(terminal, additionalRotation, entity, eyeLocation);
            if (point != null && eyeLocation.distance(point) <= terminal.getInteractDistance()) {
                return true;
            }
        }
        return false;
    }
}
