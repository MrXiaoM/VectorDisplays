package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_21_R1;

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
        return "v1_21_R1 (1.21 -- 1.21.1)";
    }

    @Override
    public @NotNull ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance) {
        return new TextHandler((codePoint, style) ->
                fontStorageGetter.apply(style.k().toString())
                        .getGlyph(codePoint, validateAdvance)
                        .getAdvance(style.c()));
    }

    @Override
    public @Nullable Integer nextEntityId() {
        return ENTITY_COUNTER == null ? null : ENTITY_COUNTER.incrementAndGet();
    }

    @Override
    public @NotNull Matrix4f textDisplayUnitSquare() {
        return new Matrix4f()
                .translate(-0.1f + .5f, -0.5f + .5f, 0f)
                .scale(8.0f, 3.65f, 1f);
    }

    @Override
    public <T> void reloadFontsViaNBTFile(InputStream stream, BiFunction<Integer, Float, T> glyph, BiConsumer<NamespacedKey, Int2ObjectMap<T>> loaded) throws IOException {
        NBTTagCompound nbt = NBTCompressedStreamTools.a(stream, NBTReadLimiter.a()); // readCompressed
        // 这个 itemType 是在 net.minecraft.nbt.NBTTagTypes 中 NBTTagCompound.a 的索引;
        int itemType = 10;
        NBTTagList nbtFonts = nbt.c("fonts", itemType); // getTagList
        for (NBTBase b : nbtFonts) {
            if (!(b instanceof NBTTagCompound fontCompound)) continue;
            String id = fontCompound.l("id"); // getString
            NBTTagCompound advances = fontCompound.p("advances"); // getCompound
            Int2ObjectMap<T> glyphs = new Int2ObjectOpenHashMap<>();
            for (String str : advances.e()) { // getKeys()
                int codePoint = Integer.parseInt(str);
                float advance = advances.j(str); // getFloat
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
