package top.mrxiaom.hologram.vector.displays.ui.api;

import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;

public abstract class ItemElement<This extends Element<This, EntityItemDisplay>> extends Element<This, EntityItemDisplay> implements EntityItemDisplayWrapper<This> {
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
        this.hologram.setRightRotation(terminal.getRotation());
    }

    @Override
    public This setScale(float scaleX, float scaleY) {
        this.hologram.setScale(scaleX, scaleY, 1.0f);
        return super.setScale(scaleX, scaleY);
    }

    @Override
    public void calculateSize() {
        // TODO: 确定大小
        this.width = 8 * HologramFont.getCharScale();
        this.height = 8 * HologramFont.getCharScale();
    }

    @Override
    public void init() {
        hologram.setRightRotation(getRotation());
        super.init();
    }
}
