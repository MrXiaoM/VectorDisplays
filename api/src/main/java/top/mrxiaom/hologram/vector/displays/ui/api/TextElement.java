package top.mrxiaom.hologram.vector.displays.ui.api;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityTextDisplay;
import top.mrxiaom.hologram.vector.displays.hologram.IEntityIdProvider;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.minecraft.font.TextDisplayLayout;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.EntityTextDisplayWrapper;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.QuaternionUtils;

public abstract class TextElement<This extends Element<This, EntityTextDisplay>>
        extends Element<This, EntityTextDisplay> implements EntityTextDisplayWrapper<This> {

    private double textWidth, textHeight;

    public TextElement(@NotNull String id) {
        this(id, IEntityIdProvider.DEFAULT);
    }

    public TextElement(@NotNull String id, @NotNull IEntityIdProvider entityIdProvider) {
        this(id, entityIdProvider, true);
    }

    public TextElement(@NotNull String id, @NotNull IEntityIdProvider entityIdProvider, boolean fixedLocation) {
        super(id, entityIdProvider, fixedLocation);
    }

    @Override
    protected EntityTextDisplay createHologram() {
        return new EntityTextDisplay(RenderMode.VIEWER_LIST, entityIdProvider)
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
        super.setScale(scaleX, scaleY);
        calculateSize();
        return $this();
    }

    /**
     * 根据悬浮字的文本，计算悬浮字在世界上的长宽尺寸
     */
    public void calculateSize() {
        Component text = getEntity().getTextAsComponent();
        TextDisplayLayout layout = TextDisplayLayout.measure(
                HologramFont.getTextRenderer(), text, getEntity().getMaxLineWidth());
        boolean background = isBackgroundOnly();
        this.textWidth = (background ? layout.backgroundWidth() : layout.width()) * Math.abs(scaleX);
        this.textHeight = (background
                ? layout.backgroundHeight(HologramUtils.LINE_HEIGHT)
                : layout.textHeight(HologramUtils.LINE_HEIGHT)) * Math.abs(scaleY);
        this.width = HologramFont.textToWorld(textWidth);
        this.height = HologramFont.textToWorld(textHeight);
    }

    /**
     * 纯空白文字的控件，渲染的是背景矩形
     */
    protected boolean isBackgroundOnly() {
        String text = getEntity().getTextWithoutColor();
        return !text.isEmpty() && text.isBlank();
    }

    protected double[] renderOriginCorrection() {
        double offset = HologramFont.textToWorld((isBackgroundOnly() ? 0.5 : 1.0) * scaleX);
        return QuaternionUtils.rotateChildrenToDouble(
                new double[] { 0, 0, 0 }, getRotation(), new double[] { -offset, 0, 0 });
    }

    @Override
    protected void setTranslationInternal(float x, float y, float z) {
        double[] correction = renderOriginCorrection();
        hologram.setTranslation(
                (float) (x + correction[0]),
                (float) (y + correction[1]),
                (float) (z + correction[2]));
    }

    @Override
    protected Location decideLocation() {
        Location location = super.decideLocation();
        if (!isFixedLocation()) {
            double[] correction = renderOriginCorrection();
            HologramUtils.add(correction, getAdditionalTranslation());
            hologram.setTranslation((float) correction[0], (float) correction[1], (float) correction[2]);
        }
        return location;
    }

    @Override
    public void updateLocation() {
        calculateSize();
        super.updateLocation();
    }

    @Override
    public void update() {
        calculateSize();
        if (terminal() != null && !getEntity().isDead()) {
            updateLocation();
        }
        super.update();
    }

    @Override
    public void init() {
        getEntity().setLeftRotation(getRotation());
        super.init();
    }
}
