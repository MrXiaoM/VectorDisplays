package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

@ApiStatus.Internal
public class Calculator {
    private final Element<?, ?> element;
    protected Calculator(Element<?, ?> element) {
        this.element = element;
    }

    public double[] decideLocation(double pX, double pY, boolean rotate) {
        double[] raw = element.decideLocationRaw(pX, pY);
        if (rotate) {
            Terminal<?> terminal = element.getTerminal();
            // TODO: 考虑 parent
            Location origin = terminal.getLocation();
            float[] rotation = terminal.getRotation();
            return QuaternionUtils.rotateChildrenToDouble(origin, rotation, raw);
        } else {
            return raw;
        }
    }
}
