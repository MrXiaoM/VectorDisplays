package top.mrxiaom.hologram.vector.displays.ui;

import net.kyori.adventure.text.Component;
import top.mrxiaom.hologram.vector.displays.minecraft.font.TextDisplayLayout;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

public class HologramFont {
    private static final double ratioX = TextDisplayLayout.PIXEL_SIZE;
    private static ITextRenderer textRenderer;

    public static void setTextRenderer(ITextRenderer textRenderer) {
        HologramFont.textRenderer = textRenderer;
    }

    public static ITextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Deprecated
    public static void recalcRatio() {
        // 已经研究明白缩放比例，不再需要计算
    }

    /**
     * 获取悬浮字的文本与世界方块尺寸之间的缩放关系
     * @see TextDisplayLayout#PIXEL_SIZE
     */
    @Deprecated
    public static double getCharScale() {
        return ratioX;
    }

    /**
     * 获取悬浮字的文本与世界方块尺寸之间的缩放关系
     * @see TextDisplayLayout#PIXEL_SIZE
     */
    public static double getRatioX() {
        return ratioX;
    }

    /**
     * 获取悬浮字文本宽度
     * @param text 文本
     */
    public static double getWidth(Component text) {
        return textRenderer.getWidth(text);
    }

    /**
     * 获取悬浮字文本在世界坐标上的宽度
     * @param text 文本
     */
    public static double getWidthToLocation(Component text) {
        return textToWorld(getWidth(text));
    }

    /**
     * 获取悬浮字文本在世界坐标上的宽度
     * @param size 文本宽度
     */
    public static Double textToWorld(double size) {
        return size * getRatioX();
    }

    /**
     * 获取世界坐标转换为悬浮字文本的宽度
     * @param size 世界宽度
     */
    public static Double worldToText(double size) {
        return size / getRatioX();
    }

    public static int getLines(Component text) {
        return textRenderer.getLines(text);
    }

    public static double getLinesToLocation(Component text) {
        int lines = getLines(text);
        return textToWorld(lines * HologramUtils.LINE_HEIGHT);
    }
}
