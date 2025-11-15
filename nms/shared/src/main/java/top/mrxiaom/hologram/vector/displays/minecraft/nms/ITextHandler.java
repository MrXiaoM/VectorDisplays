package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ITextHandler {
    int getLines(JsonElement json);
    int getLines(@Nullable String text);
    float getWidth(JsonElement json);
    float getWidth(@Nullable String text);
    JsonElement trimToWidth(JsonElement json, int maxWidth);
    String trimToWidth(String text, int maxWidth);
    String trimToWidthBackwards(String text, int maxWidth);
    List<JsonElement> wrapLines(JsonElement json, int maxWidth);
    int wrapLinesSize(JsonElement json, int maxWidth);
    int wrapLinesSize(String text, int maxWidth);
}
