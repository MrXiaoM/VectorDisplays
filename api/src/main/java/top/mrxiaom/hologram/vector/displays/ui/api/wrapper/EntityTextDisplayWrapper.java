package top.mrxiaom.hologram.vector.displays.ui.api.wrapper;

import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;

public interface EntityTextDisplayWrapper<This> extends EntityDisplayWrapper<This> {
    @Override
    @NotNull EntityTextDisplay getHologram();

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
    default This setBackgroundColor(int color) {
        getHologram().setBackgroundColor(color);
        return $this();
    }

    /**
     * 设置终端面板背景颜色
     * @param hex 十六进制颜色，使用 <code>#FFFFFFFF</code> 格式
     */
    default This setBackgroundColor(String hex) {
        setBackgroundColor(Integer.parseInt(hex.substring(1), 16));
        return $this();
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
    default This setShadow(boolean shadow) {
        getHologram().setShadow(shadow);
        return $this();
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
    default This setSeeThroughBlocks(boolean seeThroughBlocks) {
        getHologram().setSeeThroughBlocks(seeThroughBlocks);
        return $this();
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
    default This setTextAlignment(TextDisplay.TextAlignment textAlignment) {
        getHologram().setAlignment(textAlignment);
        return $this();
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
    default This setTextOpacity(byte textOpacity) {
        getHologram().setTextOpacity(textOpacity);
        return $this();
    }

}
