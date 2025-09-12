package top.mrxiaom.hologram.vector.displays.minecraft.font.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Glyph;

public class ServerFont implements Font {
    private final NamespacedKey key;
    private final Int2ObjectMap<ServerGlyph> glyphs;
    private final IntSet providedGlyphs;
    public ServerFont(NamespacedKey key, Int2ObjectMap<ServerGlyph> glyphs) {
        this.key = key;
        this.glyphs = glyphs;
        this.providedGlyphs = IntSets.unmodifiable(glyphs.keySet());
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public void close() {
        glyphs.clear();
    }

    @Nullable
    @Override
    public Glyph getGlyph(int codePoint) {
        return glyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return providedGlyphs;
    }
}
