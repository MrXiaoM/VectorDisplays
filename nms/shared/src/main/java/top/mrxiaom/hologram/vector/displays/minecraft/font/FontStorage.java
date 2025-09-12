package top.mrxiaom.hologram.vector.displays.minecraft.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.NamespacedKey;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Glyph;

import java.util.List;
import java.util.Set;

public class FontStorage implements AutoCloseable {
    private final NamespacedKey id;
    private final List<Font> fonts = Lists.newArrayList();
    private final GlyphContainer<GlyphPair> glyphCache = new GlyphContainer<>(GlyphPair[]::new, GlyphPair[][]::new);

    public FontStorage(NamespacedKey id) {
        this.id = id;
    }

    public NamespacedKey getId() {
        return id;
    }

    public void setFonts(List<Font> fonts) {
        this.closeFonts();
        this.glyphCache.clear();
        IntSet intSet = new IntOpenHashSet();

        for (Font font : fonts) {
            intSet.addAll(font.getProvidedGlyphs());
        }

        Set<Font> set = Sets.newHashSet();
        intSet.forEach((codePoint) -> {
            for (Font font : fonts) {
                Glyph glyph = font.getGlyph(codePoint);
                if (glyph != null) {
                    set.add(font);
                    break;
                }
            }
        });
        fonts.stream().filter(set::contains).forEach(this.fonts::add);
    }

    public void close() {
        this.closeFonts();
    }

    private void closeFonts() {
        for (Font font : this.fonts) {
            font.close();
        }

        this.fonts.clear();
    }

    private static boolean isAdvanceInvalid(Glyph glyph) {
        float f = glyph.getAdvance(false);
        if (!(f < 0.0F) && !(f > 32.0F)) {
            float g = glyph.getAdvance(true);
            return g < 0.0F || g > 32.0F;
        } else {
            return true;
        }
    }

    private GlyphPair findGlyph(int codePoint) {
        Glyph glyph = null;
        for (Font font : this.fonts) {
            Glyph glyph2 = font.getGlyph(codePoint);
            if (glyph2 != null) {
                if (glyph == null) {
                    glyph = glyph2;
                }

                if (!isAdvanceInvalid(glyph2)) {
                    return new GlyphPair(glyph, glyph2);
                }
            }
        }

        if (glyph != null) {
            return new GlyphPair(glyph, EmptyGlyph.MISSING);
        } else {
            return GlyphPair.MISSING;
        }
    }

    public Glyph getGlyph(int codePoint, boolean validateAdvance) {
        return this.glyphCache.computeIfAbsent(codePoint, this::findGlyph).getGlyph(validateAdvance);
    }

    private record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
        static final GlyphPair MISSING = new GlyphPair(EmptyGlyph.MISSING, EmptyGlyph.MISSING);

        Glyph getGlyph(boolean validateAdvance) {
            return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
        }
    }
}
