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
            // 应用额外旋转
            if (additionalRotate) {
                float[] ar = element.getAdditionalRotation();
                if (ar != null) {
                    double[] origin = decideLocation(element.getX(), element.getY(), true);
                    raw = QuaternionUtils.rotateChildrenToDouble(origin, ar, raw);
                }
            }
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
