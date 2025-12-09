package top.mrxiaom.hologram.vector.displays.minecraft.font.api;

import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;
import top.mrxiaom.hologram.vector.displays.minecraft.font.TextRenderer;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IFontManager extends AutoCloseable {
    NMSFactory getFactory();
    void setFactory(NMSFactory factory);
    void setForcesUnicodeFont(boolean forcesUnicodeFont);
    FontStorage getFontStorage(String id);
    List<Font> getFonts();
    void reload(File fontsFile) throws Exception;
    void reload(InputStream stream) throws Exception;
    TextRenderer createTextRenderer();
    TextRenderer createAdvanceValidatingTextRenderer();
}
