package top.mrxiaom.hologram.vector.displays.hologram.style;

import org.bukkit.entity.Display;
import top.mrxiaom.hologram.vector.displays.hologram.EntityDisplay;

public abstract class DisplayStyle<This extends DisplayStyle<This, Entity>, Entity extends EntityDisplay<Entity>> extends EntityStyle<This, Entity> {
    protected int interpolationDurationRotation = 10;
    protected int interpolationDurationTransformation = 10;
    protected float viewRange = 1.0f;
    protected Display.Billboard billboard = Display.Billboard.CENTER;
    protected int brightnessOverride = -1;

    public int getInterpolationDurationRotation() {
        return interpolationDurationRotation;
    }

    public This setInterpolationDurationRotation(int interpolationDurationRotation) {
        this.interpolationDurationRotation = interpolationDurationRotation;
        return $this();
    }

    public int getInterpolationDurationTransformation() {
        return interpolationDurationTransformation;
    }

    public This setInterpolationDurationTransformation(int interpolationDurationTransformation) {
        this.interpolationDurationTransformation = interpolationDurationTransformation;
        return $this();
    }

    public float getViewRange() {
        return viewRange;
    }

    public This setViewRange(float viewRange) {
        this.viewRange = viewRange;
        return $this();
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public This setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return $this();
    }

    public int getBrightnessOverride() {
        return brightnessOverride;
    }

    public This setBrightnessOverride(int brightnessOverride) {
        this.brightnessOverride = brightnessOverride;
        return $this();
    }

    public This setBrightnessOverride(int blockLight, int skyLight) {
        return setBrightnessOverride(blockLight << 4 | skyLight << 20);
    }

    public This setFullBrightness() {
        return setBrightnessOverride(15, 15);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        entity.setInterpolationDurationRotation(interpolationDurationRotation);
        entity.setInterpolationDurationTransformation(interpolationDurationTransformation);
        entity.setViewRange(viewRange);
        entity.setBillboard(billboard);
        entity.setBrightnessOverride(brightnessOverride);
    }
}
