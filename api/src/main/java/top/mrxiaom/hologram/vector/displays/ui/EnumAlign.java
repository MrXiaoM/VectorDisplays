package top.mrxiaom.hologram.vector.displays.ui;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * 界面对齐方式，命名以 <code>_</code> 分隔，第一位是水平对齐方式，第二位是垂直对齐方式
 */
@SuppressWarnings("CodeBlock2Expr")
public enum EnumAlign {
    LEFT_TOP((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                left(parentX, parentWidth, x, width),
                top(parentY, parentHeight, y, height),
                z
        };
    }),
    CENTER_TOP((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(parentX, parentWidth, x, width),
                top(parentY, parentHeight, y, height),
                z
        };
    }),
    RIGHT_TOP((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                right(parentX, parentWidth, x, width),
                top(parentY, parentHeight, y, height),
                z
        };
    }),
    LEFT_CENTER((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                left(parentX, parentWidth, x, width),
                centerVertical(parentY, parentHeight, y, height),
                z
        };
    }),
    CENTER((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(parentX, parentWidth, x, width),
                centerVertical(parentY, parentHeight, y, height),
                z
        };
    }),
    RIGHT_CENTER((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                right(parentX, parentWidth, x, width),
                centerVertical(parentY, parentHeight, y, height),
                z
        };
    }),
    LEFT_BOTTOM((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                left(parentX, parentWidth, x, width),
                bottom(parentY, parentHeight, y, height),
                z
        };

    }),
    CENTER_BOTTOM((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(parentX, parentWidth, x, width),
                bottom(parentY, parentHeight, y, height),
                z
        };
    }),
    RIGHT_BOTTOM((parentX, parentY, z, parentWidth, parentHeight, x, y, width, height) -> {
        return new double[] {
                right(parentX, parentWidth, x, width),
                bottom(parentY, parentHeight, y, height),
                z
        };
    });

    /*
     * 从正面看悬浮字
     *   x坐标轴指向右边
     *   y坐标轴指向上边
     *
     * 需要将其转换为
     *   x坐标轴指向右边
     *   y坐标轴指向下边
     * 的经典UI坐标系
     */

    private static double left(double parentX, double parentWidth, double x, double width) {
        return parentX + (parentWidth / 2) + x - (width / 2);
    }
    private static double centerHorizontal(double parentX, double parentWidth, double x, double width) {
        return parentX + x;
    }
    private static double right(double parentX, double parentWidth, double x, double width) {
        return parentX - (parentWidth / 2) + x + (width / 2);
    }
    private static double top(double parentY, double parentHeight, double y, double height) {
        return parentY + parentHeight - y - height;
    }
    private static double centerVertical(double parentY, double parentHeight, double y, double height) {
        return parentY + (parentHeight / 2) - y - (height / 2);
    }
    private static double bottom(double parentY, double parentHeight, double y, double height) {
        return parentY - y;
    }
    interface Impl {
        double[] get(double parentX, double parentY, double z, double parentWidth, double parentHeight, double x, double y, double width, double height);
    }
    private final Impl impl;
    EnumAlign(Impl impl) {
        this.impl = impl;
    }

    /**
     * 获取经过对齐方式转换之后的三维坐标
     * <p>
     * 由此获得的是未经旋转的原始坐标
     * @param parentX 父组件X坐标
     * @param parentY 父组件Y坐标
     * @param z Z坐标
     * @param parentWidth 父组件宽度
     * @param parentHeight 父组件高度
     * @param x 目标组件X坐标
     * @param y 目标组件Y坐标
     * @param width 目标组件宽度
     * @param height 目标组件高度
     * @return 转换后的三维坐标
     */
    public double[] get(double parentX, double parentY, double z, double parentWidth, double parentHeight, double x, double y, double width, double height) {
        return impl.get(parentX, parentY, z, parentWidth, parentHeight, x, y, width, height);
    }

    /**
     * @see EnumAlign#get(double, double, double, double, double, double, double, double, double)
     */
    public Location get(World world, double parentX, double parentY, double z, double parentWidth, double parentHeight, double x, double y, double width, double height) {
        double[] raw = get(parentX, parentY, z, parentWidth, parentHeight, x, y, width, height);
        return new Location(world, raw[0], raw[1], raw[2]);
    }
}
