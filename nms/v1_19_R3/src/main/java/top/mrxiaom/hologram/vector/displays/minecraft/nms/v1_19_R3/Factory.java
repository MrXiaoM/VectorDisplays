package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_19_R3;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
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
        } catch (ReflectiveOperationException ignored) {
        }
        if (ENTITY_COUNTER == null) {
            Logger.getLogger("VectorDisplays").warning(CAN_NOT_GET_ENTITY_COUNTER);
        }
    }

    @Override
    public @NotNull String getName() {
        return "v1_19_3 (1.19.4 -- 1.20.2)";
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
}
