package top.mrxiaom.hologram.vector.displays.minecraft.nms.mojmap.v1_21_R7;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.Entity;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

public class Factory implements NMSFactory {
    AtomicInteger ENTITY_COUNTER;
    public Factory() {
        try {
            for (Field field : Entity.class.getDeclaredFields()) {
                if (field.getType().equals(AtomicInteger.class) && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    ENTITY_COUNTER = (AtomicInteger) field.get(null);
                    break;
                }
            }
        } catch (ReflectiveOperationException ignored) {}
        if (ENTITY_COUNTER == null) {
            Logger.getLogger("VectorDisplays").warning(CAN_NOT_GET_ENTITY_COUNTER);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Mojmap 1.21.11 (1.21.11 - 1.21.x)";
    }

    @Override
    public @NotNull ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance) {
        return new TextHandler((codePoint, style) ->
                fontStorageGetter.apply(style.getFont().toString())
                        .getGlyph(codePoint, validateAdvance)
                        .getAdvance(style.isBold()));
    }

    @Override
    public @Nullable Integer nextEntityId() {
        return ENTITY_COUNTER == null ? null : ENTITY_COUNTER.incrementAndGet();
    }

    @Override
    public @NotNull Matrix4f textDisplayUnitSquare() {
        return new Matrix4f()
                .translate(-0.1f + .5f, -0.5f + .5f, 0f)
                .scale(8.0f, 4.0f, 1f);
    }

    @Override
    public <T> void reloadFontsViaNBTFile(InputStream stream, BiFunction<Integer, Float, T> glyph, BiConsumer<NamespacedKey, Int2ObjectMap<T>> loaded) throws IOException {
        CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        ListTag nbtFonts = nbt.getList("fonts").orElseThrow();
        for (Tag b : nbtFonts) {
            if (!(b instanceof CompoundTag fontCompound)) continue;
            String id = fontCompound.getString("id").orElseThrow();
            CompoundTag advances = fontCompound.getCompound("advances").orElseThrow();
            Int2ObjectMap<T> glyphs = new Int2ObjectOpenHashMap<>();
            for (String str : advances.keySet()) {
                int codePoint = Integer.parseInt(str);
                float advance = advances.getFloat(str).orElseThrow();
                glyphs.put(codePoint, glyph.apply(codePoint, advance));
            }
            NamespacedKey key;
            if (id.contains(":")) {
                key = NamespacedKey.fromString(id);
            } else {
                key = NamespacedKey.minecraft(id);
            }
            loaded.accept(key, glyphs);
        }
    }
}
