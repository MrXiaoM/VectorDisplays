package top.mrxiaom.hologram.vector.displays.minecraft.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.IFontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.server.ServerFont;
import top.mrxiaom.hologram.vector.displays.minecraft.font.server.ServerGlyph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManager implements IFontManager {
    private static final Gson gson = new GsonBuilder().create();
    public static final NamespacedKey DEFAULT_FONT_ID = NamespacedKey.minecraft("default");
    public static final NamespacedKey MISSING_STORAGE_ID = NamespacedKey.minecraft("missing");
    private final Map<String, FontStorage> fontStorages = new HashMap<>();
    private final FontStorage missingStorage;
    private final List<Font> fonts = new ArrayList<>();
    private final Map<String, String> idOverrides = new HashMap<>();
    private final Map<Integer, Float> advanceOverrides = new HashMap<>();

    public FontManager() {
        this.missingStorage = new FontStorage(MISSING_STORAGE_ID);
        this.missingStorage.setFonts(Lists.newArrayList(EmptyGlyph.blank()));
    }

    @Override
    public void setForcesUnicodeFont(boolean forcesUnicodeFont) {
        if (forcesUnicodeFont) {
            idOverrides.put("minecraft:default", "minecraft:uniform");
        } else {
            idOverrides.clear();
        }
    }

    @Override
    public FontStorage getFontStorage(String id) {
        String fontId = idOverrides.getOrDefault(id, id);
        return this.fontStorages.getOrDefault(fontId, missingStorage);
    }

    @Override
    public List<Font> getFonts() {
        return Collections.unmodifiableList(fonts);
    }

    @Override
    public void reload(File fontsFile) throws Exception {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        String name = fontsFile.getName();
        if (name.endsWith(".json")) {
            try (FileReader reader = new FileReader(fontsFile, StandardCharsets.UTF_8)) {
                reload(gson.fromJson(reader, JsonArray.class));
            }
        } else {
            try (InputStream stream = new FileInputStream(fontsFile)) {
                reloadNbt(stream);
            }
        }
    }

    @Override
    public void reload(InputStream stream) throws Exception {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        reloadNbt(stream);
    }

    public Map<Integer, Float> getAdvanceOverrides() {
        return advanceOverrides;
    }

    private ServerGlyph glyph(float advance, float boldOffset) {
        if (!Float.isFinite(advance) || !Float.isFinite(boldOffset)) {
            throw new IllegalArgumentException("字体度量必须是有限数值");
        }
        float value = advance == (int) advance
                ? advanceOverrides.getOrDefault((int) advance, advance)
                : advance;
        return new ServerGlyph(value, boldOffset);
    }

    private void reloadNbt(InputStream stream) throws Exception {
        Map<String, Object> root = FontNbtReader.read(stream);
        int format = (int) FontNbtReader.number(root, "format", 1);
        if (format != 1 && format != 2) {
            throw new IOException("不支持的字体数据版本：" + format);
        }
        for (Object tag : FontNbtReader.list(root, "fonts")) {
            Map<String, Object> data = FontNbtReader.compound(tag);
            String id = FontNbtReader.string(data, "id");
            if (format == 2) {
                Int2ObjectMap<ServerGlyph> glyphs = new Int2ObjectOpenHashMap<>();
                for (Object groupTag : FontNbtReader.list(data, "glyphs")) {
                    Map<String, Object> group = FontNbtReader.compound(groupTag);
                    ServerGlyph glyph = glyph(
                            FontNbtReader.number(group, "advance", 0),
                            FontNbtReader.number(group, "boldOffset", 1));
                    if (!(group.get("ranges") instanceof int[] ranges)) {
                        throw new IOException("字体范围必须是 IntArray：" + id);
                    }
                    if (ranges.length % 2 != 0) {
                        throw new IOException("字体范围必须成对出现：" + id);
                    }
                    for (int i = 0; i < ranges.length; i += 2) {
                        int start = ranges[i];
                        int end = ranges[i + 1];
                        if (start < 0 || end > Character.MAX_CODE_POINT || end < start) {
                            throw new IOException("字体码点范围无效：" + id);
                        }
                        for (int code = start; code <= end; code++) {
                            if (glyphs.put(code, glyph) != null) {
                                throw new IOException("字体码点范围重复：" + id);
                            }
                        }
                    }
                }
                addFont(id, glyphs);
                continue;
            }
            Map<String, Object> advances = FontNbtReader.compound(data.get("advances"));
            Map<String, Object> offsets = data.containsKey("boldOffsets")
                    ? FontNbtReader.compound(data.get("boldOffsets"))
                    : Collections.emptyMap();
            Int2ObjectMap<ServerGlyph> glyphs = new Int2ObjectOpenHashMap<>();
            for (String code : advances.keySet()) {
                int codePoint = Integer.parseInt(code);
                float offset = FontNbtReader.number(offsets, code, 1.0f);
                glyphs.put(codePoint, glyph(FontNbtReader.number(advances, code, 0), offset));
            }
            addFont(id, glyphs);
        }
        requireDefaultFont();
    }

    private void addFont(String id, Int2ObjectMap<ServerGlyph> glyphs) {
        NamespacedKey key = id.contains(":")
                ? NamespacedKey.fromString(id)
                : NamespacedKey.minecraft(id);
        if (key == null) {
            throw new IllegalArgumentException("无效的字体标识：" + id);
        }
        FontStorage storage = new FontStorage(key);
        ServerFont font = new ServerFont(key, glyphs);
        storage.setFonts(Lists.newArrayList(font));
        fonts.add(font);
        fontStorages.put(key.toString(), storage);
    }

    private void requireDefaultFont() {
        if (!fontStorages.containsKey(DEFAULT_FONT_ID.toString())) {
            throw new IllegalStateException("默认字体加载失败");
        }
    }

    /**
     * 重载字体配置。其配置格式如下，其中字符使用政数 codePoint，与 Unicode 码基本一致
     * <pre><code>
     * [
     *     {
     *         "id": "minecraft:uniform", // 默认字体
     *         "advances": {
     *             "字符宽度": [ 字符1, 字符2, 字符3... ]
     *         }
     *     }
     * ]
     * </code></pre>
     */
    private void reload(JsonArray array) {
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            String id = obj.get("id").getAsString();
            JsonObject advances = obj.get("advances").getAsJsonObject();
            Int2ObjectMap<ServerGlyph> glyphs = new Int2ObjectOpenHashMap<>();
            Map<Integer, Float> offsets = new HashMap<>();
            if (obj.has("boldOffsets")) {
                JsonObject boldOffsets = obj.getAsJsonObject("boldOffsets");
                for (String offset : boldOffsets.keySet()) {
                    for (JsonElement code : boldOffsets.getAsJsonArray(offset)) {
                        offsets.put(code.getAsInt(), Float.parseFloat(offset));
                    }
                }
            }
            for (String key : advances.keySet()) {
                float advance = Float.parseFloat(key);
                for (JsonElement code : advances.getAsJsonArray(key)) {
                    int codePoint = code.getAsInt();
                    float offset = offsets.getOrDefault(codePoint, 1.0f);
                    glyphs.put(codePoint, glyph(advance, offset));
                }
            }
            addFont(id, glyphs);
        }
        requireDefaultFont();
    }

    @Override
    public TextRenderer createTextRenderer() {
        return new TextRenderer(this, new AdventureTextHandler((codePoint, style) -> {
            String id = style.font() == null
                    ? DEFAULT_FONT_ID.toString()
                    : style.font().asString();
            return getFontStorage(id).getGlyph(codePoint)
                    .getAdvance(style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE);
        }));
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fonts.forEach(Font::close);
        this.missingStorage.close();
    }
}
