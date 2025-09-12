package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_20_R3;

import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.TextHandlerFactory;

import java.util.function.Function;

public class Factory implements TextHandlerFactory {

    @Override
    public String getName() {
        return "v1_20_R3 (1.20.3 -- 1.20.4)";
    }

    @Override
    public ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance) {
        return new TextHandler((codePoint, style) ->
                fontStorageGetter.apply(style.k().toString())
                        .getGlyph(codePoint, validateAdvance)
                        .getAdvance(style.b()));
    }
}
