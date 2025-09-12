package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;

import java.util.function.Function;

public interface TextHandlerFactory {
    String getName();
    ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance);
}
