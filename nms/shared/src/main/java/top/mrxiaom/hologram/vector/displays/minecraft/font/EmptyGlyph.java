package top.mrxiaom.hologram.vector.displays.minecraft.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Glyph;

public enum EmptyGlyph implements Glyph {
    MISSING(5);
    final int width;

    EmptyGlyph(int width) {
        this.width = width;
    }

    public float getAdvance() {
        return (float)(this.width + 1);
    }

    public static Font blank() {
        return new Font() {
            @Override
            public NamespacedKey getKey() {
                return NamespacedKey.minecraft("blank");
            }
            @NotNull
            public Glyph getGlyph(int codePoint) {
                return MISSING;
            }
            @Override
            public IntSet getProvidedGlyphs() {
                return IntSets.EMPTY_SET;
            }
        };
    }
}
