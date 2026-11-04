package top.mrxiaom.hologram.vector.displays.minecraft.font;

import net.kyori.adventure.text.Component;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;

import java.util.List;

/**
 * 展示实体的客户端像素布局计算器。
 * 文本行高为 9，行距为 10；背景比文字布局宽、高各多一个像素。
 */
public class TextDisplayLayout {
    public static final double PIXEL_SIZE = 0.025;
    public static final int LINE_HEIGHT = 10;

    private final int width;
    private final int lines;

    public TextDisplayLayout(int width, int lines) {
        this.width = width;
        this.lines = lines;
    }

    public int width() {
        return width;
    }

    public int lines() {
        return lines;
    }

    public static TextDisplayLayout measure(ITextRenderer renderer, Component text, int maxLineWidth) {
        List<Component> lines = renderer.wrapLines(text, maxLineWidth);
        int width = 0;
        for (Component line : lines) {
            width = Math.max(width, renderer.getWidth(line));
        }
        return new TextDisplayLayout(width, lines.size());
    }

    public int textHeight() {
        return Math.max(0, lines * LINE_HEIGHT - 1);
    }

    public double textHeight(double lineHeight) {
        return Math.max(0, lines * lineHeight - 1);
    }

    public double backgroundHeight(double lineHeight) {
        return lines * lineHeight;
    }

    public int backgroundWidth() {
        return width + 1;
    }

    public int backgroundHeight() {
        return lines * LINE_HEIGHT;
    }

    /**
     * 客户端背景中心相对实体原点偏右半像素，文字布局则偏右一个像素。
     */
    public double originOffsetX(boolean background) {
        return background ? 0.5 : 1.0;
    }
}
