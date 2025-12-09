package top.mrxiaom.hologram.vector.displays.minecraft.font;

import com.google.common.collect.Lists;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.NamespacedKey;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.IFontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.server.ServerFont;
import top.mrxiaom.hologram.vector.displays.minecraft.font.server.ServerGlyph;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FontManager implements IFontManager {
    private static final Gson gson = new GsonBuilder().create();
    public static final NamespacedKey DEFAULT_FONT_ID = NamespacedKey.minecraft("default");
    public static final NamespacedKey MISSING_STORAGE_ID = NamespacedKey.minecraft("missing");
    private final Map<String, FontStorage> fontStorages = new HashMap<>();
    private final FontStorage missingStorage;
    private final List<Font> fonts = new ArrayList<>();
    private final Map<String, String> idOverrides = new HashMap<>();
    private NMSFactory factory;
    public FontManager(NMSFactory factory) {
        this.factory = factory;
        this.missingStorage = new FontStorage(MISSING_STORAGE_ID);
        this.missingStorage.setFonts(Lists.newArrayList(EmptyGlyph.blank()));
    }

    @Override
    public NMSFactory getFactory() {
        return factory;
    }

    @Override
    public void setFactory(NMSFactory factory) {
        this.factory = factory;
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
        try (FileReader reader = new FileReader(fontsFile, StandardCharsets.UTF_8)) {
            reload(gson.fromJson(reader, JsonArray.class));
        }
    }

    @Override
    public void reload(InputStream stream) throws Exception {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            reload(gson.fromJson(reader, JsonArray.class));
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
        List<Font> allProviders = new ArrayList<>();
        for (JsonElement e : array) {
            JsonObject obj = e.getAsJsonObject();
            String id = obj.get("id").getAsString();
            JsonObject advances = obj.get("advances").getAsJsonObject();
            Int2ObjectMap<ServerGlyph> glyphs = new Int2ObjectOpenHashMap<>();
            for (String key : advances.keySet()) {
                float advance = Float.parseFloat(key);
                JsonArray a = advances.get(key).getAsJsonArray();
                for (JsonElement codePoint : a) {
                    glyphs.put(codePoint.getAsInt(), new ServerGlyph(advance));
                }
            }
            NamespacedKey key;
            if (id.contains(":")) {
                key = NamespacedKey.fromString(id);
            } else {
                key = NamespacedKey.minecraft(id);
            }

            FontStorage fontStorage = new FontStorage(key);
            ServerFont font = new ServerFont(key, glyphs);
            allProviders.add(font);
            fontStorage.setFonts(Lists.newArrayList(font));
            this.fontStorages.put(id, fontStorage);
        }
        this.fonts.addAll(allProviders);
        if (!this.fontStorages.containsKey(DEFAULT_FONT_ID.toString())) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    @Override
    public TextRenderer createTextRenderer() {
        return new TextRenderer(this, factory.create(this::getFontStorage, false));
    }

    @Override
    public TextRenderer createAdvanceValidatingTextRenderer() {
        return new TextRenderer(this, factory.create(this::getFontStorage, true));
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fonts.forEach(Font::close);
        this.missingStorage.close();
    }
}
