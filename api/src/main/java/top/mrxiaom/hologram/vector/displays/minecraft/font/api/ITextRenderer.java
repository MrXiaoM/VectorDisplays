package top.mrxiaom.hologram.vector.displays.minecraft.font.api;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface ITextRenderer {
    int getLines(String text);
    int getLines(Component text);
    int getWidth(String text);
    int getWidth(Component text);
    int getSpaceCount(float width);
    String trimToWidth(String text, int maxWidth, boolean backwards);
    String trimToWidth(String text, int maxWidth);
    Component trimToWidth(Component text, int width);
    int getWrappedLinesHeight(String text, int maxWidth);
    int getWrappedLinesHeight(Component text, int maxWidth);
    List<Component> wrapLines(Component text, int width);
}
