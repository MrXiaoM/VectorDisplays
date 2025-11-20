package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontStorage;

import java.util.function.Function;

public interface NMSFactory {
    @NotNull String getName();
    @NotNull ITextHandler create(Function<String, FontStorage> fontStorageGetter, boolean validateAdvance);
    @Nullable Integer nextEntityId();
    @NotNull Matrix4f textDisplayUnitSquare();
}
