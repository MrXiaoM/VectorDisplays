package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface NMSFactory {
    String CAN_NOT_GET_ENTITY_COUNTER = "无法取得 ENTITY_COUNTER，将使用 EntityLib 默认的虚拟实体 ID 生成器。";

    @NotNull
    String getName();

    @Nullable
    Integer nextEntityId();

    @NotNull
    Matrix4f textDisplayUnitSquare();
}
