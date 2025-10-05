package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityItemDisplayWrapper;

public abstract class ItemElement<This extends Element<This, EntityItemDisplay>> extends Element<This, EntityItemDisplay> implements EntityItemDisplayWrapper<This> {
    protected float scaleZ = 1.0f;
    public ItemElement(@NotNull String id) {
        super(id);
    }

    @Override
    protected EntityItemDisplay createHologram() {
        return new EntityItemDisplay(RenderMode.VIEWER_LIST)
                .setInterpolationDurationTransformation(3)
                .setInterpolationDurationRotation(0)
                .setBillboard(Display.Billboard.FIXED)
                .removeAllViewers();
    }

    /**
     * 获取悬浮字实例 (尽量不要使用这个方法)
     */
    @NotNull
    @Override
    public EntityItemDisplay getHologram() {
        return hologram;
    }

    @Override
    protected void setTerminal(@NotNull Terminal<?> terminal) {
        super.setTerminal(terminal);
        this.hologram.setRightRotation(getRotation());
    }

    @Override
    public This setScale(float scale) {
        return setScale(scale, scale, scale);
    }

    public This setScale(float scaleX, float scaleY, float scaleZ) {
        this.hologram.setScale(scaleX, scaleY, scaleZ);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        return $this();
    }

    public This setScaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
        return $this();
    }

    public float getScaleZ() {
        return scaleZ;
    }

    @Override
    @Deprecated
    public This setScale(float scaleX, float scaleY) {
        return setScale(scaleX, scaleY, scaleX);
    }

    @Override
    public void calculateSize() {
        // TODO: 确定大小
        this.width = 32 * HologramFont.getCharScale() * scaleX;
        this.height = 32 * HologramFont.getCharScale() * scaleY;
    }

    @Override
    public void init() {
        hologram.setRightRotation(getRotation());
        super.init();
    }
}
