package top.mrxiaom.hologram.vector.displays.hologram.style;

import org.bukkit.entity.TextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;

public class TextDisplayStyle extends DisplayStyle<TextDisplayStyle, EntityTextDisplay> {

    private boolean shadow = true;
    private int maxLineWidth = 10000;
    private int backgroundColor;
    private boolean seeThroughBlocks = false;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private byte textOpacity = (byte) -1;

    public boolean isShadow() {
        return shadow;
    }

    public TextDisplayStyle setShadow(boolean shadow) {
        this.shadow = shadow;
        return $this();
    }

    public int getMaxLineWidth() {
        return maxLineWidth;
    }

    public TextDisplayStyle setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
        return $this();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public TextDisplayStyle setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return $this();
    }

    public boolean isSeeThroughBlocks() {
        return seeThroughBlocks;
    }

    public TextDisplayStyle setSeeThroughBlocks(boolean seeThroughBlocks) {
        this.seeThroughBlocks = seeThroughBlocks;
        return $this();
    }

    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    public TextDisplayStyle setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return $this();
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public TextDisplayStyle setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return $this();
    }

    @Override
    public void sync(EntityTextDisplay entity) {
        super.sync(entity);
        entity.setShadow(shadow);
        entity.setMaxLineWidth(maxLineWidth);
        entity.setBackgroundColor(backgroundColor);
        entity.setSeeThroughBlocks(seeThroughBlocks);
        entity.setAlignment(alignment);
        entity.setTextOpacity(textOpacity);
    }
}
