package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

import java.util.Optional;
import java.util.UUID;

public class EntityTextDisplay extends EntityDisplay<EntityTextDisplay> {

    protected Component text = Component.text("");

    private boolean shadow = true;
    private int maxLineWidth = 200;
    private int backgroundColor;
    private boolean seeThroughBlocks = false;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private byte textOpacity = (byte) -1;

    public EntityTextDisplay(RenderMode renderMode) {
        super(renderMode);
        startRunnable();
    }

    public EntityTextDisplay() {
        this(RenderMode.NEARBY);
    }

    @Override
    public PacketWrapper<?> buildSpawnPacket() {
        return new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
    }

    protected TextDisplayMeta createMeta() {
        TextDisplayMeta meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, EntityTypes.TEXT_DISPLAY);
        applyDisplayMeta(meta);
        meta.setText(getTextAsComponent());
        meta.setLineWidth(this.maxLineWidth);
        meta.setBackgroundColor(this.backgroundColor);
        meta.setTextOpacity(this.textOpacity);
        meta.setShadow(this.shadow);
        meta.setSeeThrough(this.seeThroughBlocks);
        switch (this.alignment) {
            case LEFT -> meta.setAlignLeft(true);
            case RIGHT -> meta.setAlignRight(true);
        }
        return meta;
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return legacyText.serialize(text);
    }

    public String getTextWithoutColor() {
        return HologramUtils.toPlain(text);
    }

    public EntityTextDisplay setText(String text) {
        this.text = Component.text(replaceFontImages(text));
        return this;
    }

    public EntityTextDisplay setText(Component component) {
        this.text = component;
        return this;
    }

    private String replaceFontImages(String string) {
        return HologramAPI.getReplaceText().replace(string);
    }


    public boolean isShadow() {
        return shadow;
    }

    public EntityTextDisplay setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public int getMaxLineWidth() {
        return maxLineWidth;
    }

    public EntityTextDisplay setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public EntityTextDisplay setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public boolean isSeeThroughBlocks() {
        return seeThroughBlocks;
    }

    public EntityTextDisplay setSeeThroughBlocks(boolean seeThroughBlocks) {
        this.seeThroughBlocks = seeThroughBlocks;
        return this;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    public EntityTextDisplay setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public EntityTextDisplay setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

}
