package top.mrxiaom.hologram.vector.displays.minecraft.font;

import com.google.gson.JsonElement;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class TextRenderer implements ITextRenderer {
    private final ITextHandler handler;
    private final GsonComponentSerializer serializer = BukkitComponentSerializer.gson();

    public TextRenderer(FontManager fontManager, ITextHandler handler) {
        this.handler = handler;
    }

    @Override
    public int getLines(String text) {
        return handler.getLines(text);
    }

    @Override
    public int getLines(Component text) {
        JsonElement json = serializer.serializeToTree(text);
        return handler.getLines(json);
    }

    @Override
    public int getWidth(String text) {
        return ceil(this.handler.getWidth(text));
    }

    @Override
    public int getWidth(Component text) {
        JsonElement json = serializer.serializeToTree(text);
        return ceil(handler.getWidth(json));
    }

    @Override
    public int getSpaceCount(float width) {
        int spaceWidth = getWidth(" ");
        return (int) Math.floor(width / spaceWidth);
    }

    @Override
    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, maxWidth) : this.handler.trimToWidth(text, maxWidth);
    }

    @Override
    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, maxWidth);
    }

    @Override
    public Component trimToWidth(Component text, int width) {
        JsonElement trimmed = this.handler.trimToWidth(serializer.serializeToTree(text), width);
        return serializer.deserializeFromTree(trimmed);
    }

    @Override
    public int getWrappedLinesHeight(String text, int maxWidth) {
        return 9 * this.handler.wrapLinesSize(text, maxWidth);
    }

    @Override
    public int getWrappedLinesHeight(Component text, int maxWidth) {
        return 9 * this.handler.wrapLinesSize(serializer.serializeToTree(text), maxWidth);
    }

    @Override
    public List<Component> wrapLines(Component text, int width) {
        List<JsonElement> lines = this.handler.wrapLines(serializer.serializeToTree(text), width);
        List<Component> components = new ArrayList<>();
        for (JsonElement line : lines) {
            components.add(serializer.deserializeFromTree(line));
        }
        return components;
    }

    public ITextHandler getTextHandler() {
        return this.handler;
    }

    private static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }
}
