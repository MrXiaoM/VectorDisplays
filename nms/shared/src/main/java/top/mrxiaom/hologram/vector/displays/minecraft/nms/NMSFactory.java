package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface NMSFactory {
    @NotNull String getName();
    @NotNull ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance);
    @Nullable Integer nextEntityId();
    @NotNull Matrix4f textDisplayUnitSquare();
    <T> void reloadFontsViaNBTFile(InputStream stream, BiFunction<Integer, Float, T> glyph, BiConsumer<NamespacedKey, Int2ObjectMap<T>> loaded) throws IOException;
}
