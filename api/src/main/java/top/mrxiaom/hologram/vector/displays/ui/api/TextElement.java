package top.mrxiaom.hologram.vector.displays.ui.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;
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
        return getEntity();
    }

    /**
     * 获取该元素的悬浮字在文本坐标系的宽度
     */
    public double getTextWidth() {
        return textWidth;
    }

    /**
     * 获取该元素的悬浮字在文本坐标系的高度
     */
    public double getTextHeight() {
        return textHeight;
    }

    @Override
    public This setScale(float scaleX, float scaleY) {
        this.getEntity().setScale(scaleX, scaleY, 1.0f);
        return super.setScale(scaleX, scaleY);
    }

    /**
     * 根据悬浮字的文本，计算悬浮字在世界上的长宽尺寸
     */
    public void calculateSize() {
        calculateSize(false);
    }

    protected void calculateSize(boolean addSpaces) {
        ITextRenderer textRenderer = HologramFont.getTextRenderer();
        Component text = getEntity().getTextAsComponent();
        int maxWidth = 0, lines = 0;
        for (String s : HologramUtils.toPlain(text).split("\n")) {
            int width = textRenderer.getWidth(s) + (addSpaces ? s.length() : 0);
            if (width > maxWidth) maxWidth = width;
            lines++;
        }
        this.textWidth = maxWidth * scaleX;
        this.textHeight = lines * HologramUtils.LINE_HEIGHT * scaleY;
        this.width = textWidth * HologramFont.getCharScale();
        this.height = textHeight * HologramFont.getCharScale();
    }

    @Override
    public void init() {
        getEntity().setLeftRotation(getRotation());
        super.init();
    }
}
