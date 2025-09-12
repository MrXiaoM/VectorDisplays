package top.mrxiaom.hologram.vector.displays.minecraft.font.api;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public interface Font extends AutoCloseable {
    default void close() {
    }

    NamespacedKey getKey();

    @Nullable
    default Glyph getGlyph(int codePoint) {
        return null;
    }

    IntSet getProvidedGlyphs();
}
