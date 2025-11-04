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
        return decideLocation(pX, pY, rotate, false);
    }
    public double[] decideLocation(double pX, double pY, boolean rotate, boolean additionalRotate) {
        double[] raw = element.decideLocationRaw(pX, pY);
        if (rotate) {
            Terminal<?> terminal = element.getTerminal();
            Location origin = terminal.getLocation();
            float[] ar = element.getAdditionalRotation();
            float[] rotation = additionalRotate && ar != null
                    ? QuaternionUtils.multiplyF(terminal.getRotation(), ar)
                    : terminal.getRotation();
            return QuaternionUtils.rotateChildrenToDouble(origin, rotation, raw);
        } else {
            return raw;
        }
    }
}
