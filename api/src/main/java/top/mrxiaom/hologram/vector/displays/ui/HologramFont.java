package top.mrxiaom.hologram.vector.displays.ui;

import net.kyori.adventure.text.Component;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

public class HologramFont {
    private static ITextRenderer textRenderer;
    private static double charScale = 0.01140684410646387837470543162399;
    public static void setTextRenderer(ITextRenderer textRenderer) {
        HologramFont.textRenderer = textRenderer;
    }

    public static ITextRenderer getTextRenderer() {
        return textRenderer;
    }
    /**
     * 设置悬浮字的文本与世界方块尺寸之间的缩放关系
     * @param scaleSample 样例文本，例如空格
     * @param sampleCount 多少个样例文本的宽度，才有 1 个方块的大小
     */
    public static void setCharScale(String scaleSample, double sampleCount) {
        // 获取样例字符宽度
        int sampleWidth = textRenderer.getWidth(scaleSample);
        // 坐标缩放比例 = 方块数量(1) / (一个方块边长所用的字符数量 * 单个字符宽度)
        charScale = 1.0 / (sampleCount * sampleWidth);
    }

    /**
     * 获取悬浮字的文本与世界方块尺寸之间的缩放关系
     */
    public static double getCharScale() {
        return charScale;
    }

    /**
     * 获取悬浮字文本宽度
     * @param text 文本
     */
    public static double getWidth(Component text) {
        int max = 0;
        for (String s : HologramUtils.toPlain(text).split("\n")) {
            int width = textRenderer.getWidth(s);
            if (width > max) max = width;
        }
        return max;
    }

    /**
     * 获取悬浮字文本在世界坐标上的宽度
     * @param text 文本
     */
    public static double getWidthToLocation(Component text) {
        return getWidth(text) * charScale;
    }
}
