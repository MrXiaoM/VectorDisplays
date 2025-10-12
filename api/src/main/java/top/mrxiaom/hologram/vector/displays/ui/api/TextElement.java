package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

public abstract class TextElement<This extends Element<This, EntityTextDisplay>> extends Element<This, EntityTextDisplay> implements EntityTextDisplayWrapper<This> {

    private double textWidth, textHeight;

    public TextElement(@NotNull String id) {
        super(id);
    }

    @Override
    protected EntityTextDisplay createHologram() {
        return new EntityTextDisplay(RenderMode.VIEWER_LIST)
                .setInterpolationDurationTransformation(3)
                .setInterpolationDurationRotation(0)
                .setAlignment(TextDisplay.TextAlignment.LEFT)
                .setBillboard(Display.Billboard.FIXED);
    }

    /**
     * 获取悬浮字实例 (尽量不要使用这个方法)
     */
    @NotNull
    @Override
    public EntityTextDisplay getHologram() {
        return hologram;
    }

    /**
     * 获取该元素的悬浮字宽度
     */
    public double getTextWidth() {
        return textWidth;
    }

    /**
     * 获取该元素的悬浮字高度
     */
    public double getTextHeight() {
        return textHeight;
    }

    @Override
    public This setScale(float scaleX, float scaleY) {
        this.hologram.setScale(scaleX, scaleY, 1.0f);
        return super.setScale(scaleX, scaleY);
    }

    /**
     * 根据悬浮字的文本，计算悬浮字在世界上的长宽尺寸
     */
    public void calculateSize() {
        this.textWidth = HologramFont.getWidth(hologram.getTextAsComponent()) * scaleX;
        this.textHeight = HologramUtils.getLines(hologram) * HologramUtils.LINE_HEIGHT * scaleY;
        this.width = textWidth * HologramFont.getCharScale();
        this.height = textHeight * HologramFont.getCharScale();
    }

    @Override
    public void init() {
        hologram.setLeftRotation(getRotation());
        super.init();
    }
}
