package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.TextHologram;

/**
 * 包装一些悬浮字的方法，减少开发者调用 getHologram 的机会
 */
public interface HologramWrapper {
    @NotNull TextHologram getHologram();

    /**
     * 设置悬浮字文本亮度
     * @param blockLight 方块亮度 (0-15)
     * @param skyLight 天空亮度 (0-15)
     */
    default void setBrightness(int blockLight, int skyLight) {
        // https://github.com/Tofaa2/EntityLib/blob/2.4.11/api/src/main/java/me/tofaa/entitylib/meta/display/AbstractDisplayMeta.java#L133-L140
        getHologram().setBrightnessOverride(blockLight << 4 | skyLight << 20);
    }

    /**
     * 设置悬浮字高亮，无论在多黑暗的环境下均使用最高亮度
     */
    default void setFullBrightness() {
        setBrightness(15, 15);
    }

    /**
     * 获取终端面板背景颜色
     */
    default int getBackgroundColor() {
        return getHologram().getBackgroundColor();
    }

    /**
     * 设置终端面板背景颜色
     * @param color 十进制颜色，可用 <code>0xFFFFFFFF</code> 格式使用十六进制颜色
     */
    default void setBackgroundColor(int color) {
        getHologram().setBackgroundColor(color);
    }

    /**
     * 设置终端面板背景颜色
     * @param hex 十六进制颜色，使用 <code>#FFFFFFFF</code> 格式
     */
    default void setBackgroundColor(String hex) {
        setBackgroundColor(Integer.parseInt(hex.substring(1), 16));
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
    default void setViewRange(int viewRange) {
        getHologram().setViewRange(viewRange);
    }

    /**
     * 获取悬浮字文字是否有阴影
     */
    default boolean isShadow() {
        return getHologram().isShadow();
    }

    /**
     * 设置悬浮字文字是否有阴影
     */
    default void setShadow(boolean shadow) {
        getHologram().setShadow(shadow);
    }

    /**
     * 获取悬浮字是否穿过方块可视
     */
    default boolean isSeeThroughBlocks() {
        return getHologram().isSeeThroughBlocks();
    }

    /**
     * 设置悬浮字是否穿过方块可视
     */
    default void setSeeThroughBlocks(boolean seeThroughBlocks) {
        getHologram().setSeeThroughBlocks(seeThroughBlocks);
    }

    /**
     * 获取悬浮字的文字对齐方式
     */
    default TextDisplay.TextAlignment getTextAlignment() {
        return getHologram().getAlignment();
    }

    /**
     * 设置悬浮字的文字对齐方式
     */
    default void setTextAlignment(TextDisplay.TextAlignment textAlignment) {
        getHologram().setAlignment(textAlignment);
    }

    /**
     * 获取悬浮字的文字不透明度
     */
    default byte getTextOpacity() {
        return getHologram().getTextOpacity();
    }

    /**
     * 设置悬浮字的文字不透明度
     */
    default void setTextOpacity(byte textOpacity) {
        getHologram().setTextOpacity(textOpacity);
    }

    /**
     * 获取悬浮字的虚拟实体是否存活
     */
    default boolean isDead() {
        return getHologram().isDead();
    }
}
