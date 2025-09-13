package top.mrxiaom.hologram.vector.displays.ui;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * 界面对齐方式，命名以 <code>_</code> 分隔，第一位是水平对齐方式，第二位是垂直对齐方式
 */
@SuppressWarnings("CodeBlock2Expr")
public enum EnumAlign {
    LEFT_TOP((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                left(rootX, rootWidth, x, width),
                top(rootY, rootHeight, y, height),
                z
        };
    }),
    CENTER_TOP((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(rootX, rootWidth, x, width),
                top(rootY, rootHeight, y, height),
                z
        };
    }),
    RIGHT_TOP((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                right(rootX, rootWidth, x, width),
                top(rootY, rootHeight, y, height),
                z
        };
    }),
    LEFT_CENTER((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                left(rootX, rootWidth, x, width),
                centerVertical(rootY, rootHeight, y, height),
                z
        };
    }),
    CENTER((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(rootX, rootWidth, x, width),
                centerVertical(rootY, rootHeight, y, height),
                z
        };
    }),
    RIGHT_CENTER((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                right(rootX, rootWidth, x, width),
                centerVertical(rootY, rootHeight, y, height),
                z
        };
    }),
    LEFT_BOTTOM((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                left(rootX, rootWidth, x, width),
                bottom(rootY, rootHeight, y, height),
                z
        };

    }),
    CENTER_BOTTOM((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                centerHorizontal(rootX, rootWidth, x, width),
                bottom(rootY, rootHeight, y, height),
                z
        };
    }),
    RIGHT_BOTTOM((rootX, rootY, z, rootWidth, rootHeight, x, y, width, height) -> {
        return new double[] {
                right(rootX, rootWidth, x, width),
                bottom(rootY, rootHeight, y, height),
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

    private static double left(double rootX, double rootWidth, double x, double width) {
        return rootX - (rootWidth / 2) + x + (width / 2);
    }
    private static double centerHorizontal(double rootX, double rootWidth, double x, double width) {
        return rootX + x;
    }
    private static double right(double rootX, double rootWidth, double x, double width) {
        return rootX + (rootWidth / 2) + x - (width / 2);
    }
    private static double top(double rootY, double rootHeight, double y, double height) {
        return rootY + rootHeight - y;
    }
    private static double centerVertical(double rootY, double rootHeight, double y, double height) {
        return rootY + (rootHeight / 2) - y - (height / 2);
    }
    private static double bottom(double rootY, double rootHeight, double y, double height) {
        return rootY - y;
    }
    interface Impl {
        double[] get(double rootX, double rootY, double z, double rootWidth, double rootHeight, double x, double y, double width, double height);
    }
    private final Impl impl;
    EnumAlign(Impl impl) {
        this.impl = impl;
    }
    public double[] get(double rootX, double rootY, double z, double rootWidth, double rootHeight, double x, double y, double width, double height) {
        return impl.get(rootX, rootY, z, rootWidth, rootHeight, x, y, width, height);
    }
    public Location get(World world, double rootX, double rootY, double z, double rootWidth, double rootHeight, double x, double y, double width, double height) {
        double[] raw = get(rootX, rootY, z, rootWidth, rootHeight, x, y, width, height);
        return new Location(world, raw[0], raw[1], raw[2]);
    }
}
