package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_21_R4;

import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.TextHandlerFactory;

import java.util.function.Function;

public class Factory implements TextHandlerFactory {

    @Override
    public String getName() {
        return "v1_21_R4 (1.21.5 - 1.21.x)";
    }

    @Override
    public ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance) {
        return new TextHandler((codePoint, style) ->
                fontStorageGetter.apply(style.l().toString())
                        .getGlyph(codePoint, validateAdvance)
                        .getAdvance(style.c()));
    }
}
