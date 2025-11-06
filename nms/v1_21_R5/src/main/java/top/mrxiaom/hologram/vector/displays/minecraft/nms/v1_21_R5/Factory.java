package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_21_R5;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
    }

    @Override
    public @NotNull String getName() {
        return "v1_21_R5 (1.21.6 - 1.21.8)";
    }

    @Override
    public @NotNull ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance) {
        return new TextHandler((codePoint, style) ->
                fontStorageGetter.apply(style.l().toString())
                        .getGlyph(codePoint, validateAdvance)
                        .getAdvance(style.c()));
    }

    @Override
    public @Nullable Integer nextEntityId() {
        return ENTITY_COUNTER == null ? null : ENTITY_COUNTER.incrementAndGet();
    }
}
