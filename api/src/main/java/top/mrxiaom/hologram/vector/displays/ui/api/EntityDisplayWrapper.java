package top.mrxiaom.hologram.vector.displays.ui.api;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityDisplay;

/**
 * 包装一些悬浮字的方法，减少开发者调用 getHologram 的机会
 */
public interface EntityDisplayWrapper<This> {
    @NotNull EntityDisplay<?> getHologram();
    This $this();

    /**
     * 设置悬浮字文本亮度
     * @param blockLight 方块亮度 (0-15)
     * @param skyLight 天空亮度 (0-15)
     */
    default This setBrightness(int blockLight, int skyLight) {
        // https://github.com/Tofaa2/EntityLib/blob/2.4.11/api/src/main/java/me/tofaa/entitylib/meta/display/AbstractDisplayMeta.java#L133-L140
        getHologram().setBrightnessOverride(blockLight << 4 | skyLight << 20);
        return $this();
    }

    /**
     * 设置悬浮字高亮，无论在多黑暗的环境下均使用最高亮度
     */
    default This setFullBrightness() {
        setBrightness(15, 15);
        return $this();
    }

    /**
     * 获取悬浮字可视距离
     */
    default double getViewRange() {
        return getHologram().getViewRange();
    }

    /**
     * 设置悬浮字可视距离
     */
    default This setViewRange(int viewRange) {
        getHologram().setViewRange(viewRange);
        return $this();
    }

    /**
     * 获取悬浮字的虚拟实体是否存活
     */
    default boolean isDead() {
        return getHologram().isDead();
    }
}
