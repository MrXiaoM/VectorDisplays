package top.mrxiaom.hologram.vector.displays.ui.widget;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.*;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.Hoverable;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.hologram.style.TextDisplayStyle;
import top.mrxiaom.hologram.vector.displays.ui.event.HoverStateChange;
import top.mrxiaom.hologram.vector.displays.utils.TriangleUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Triangle extends Element<Triangle, EntityNone> implements Hoverable {
    private final Map<Integer, EntityTextDisplay> entities = new HashMap<>();
    private final TextDisplayStyle style = new TextDisplayStyle();
    private float[] pos1, pos2, pos3;
    private boolean hoverState = false;
    private HoverStateChange<Triangle> hoverStateChange;
    public Triangle(@NotNull String id) {
        this(id, IEntityIdProvider.DEFAULT);
    }
    public Triangle(@NotNull String id, @NotNull IEntityIdProvider entityIdProvider) {
        super(id, entityIdProvider);
        style.setBackgroundColor(0xFFFFFFFF);
        style.setInterpolationDurationTransformation(3);
        style.setInterpolationDurationRotation(0);
        style.setBillboard(Display.Billboard.FIXED);
    }

    public Triangle configureStyle(Consumer<TextDisplayStyle> consumer) {
        consumer.accept(style);
        style.syncAll();
        return this;
    }

    public TextDisplayStyle getStyle() {
        return style;
    }

    @Override
    public void init() {
        Terminal<?> terminal = terminal();
        if (terminal != null) {
            getEntity().setRenderMode(terminal.getRenderMode());
            HologramAPI.getHologram().spawn(getEntity(), terminal.getLocation());
            calculateTriangle(terminal);
        }
    }

    @Override
    public void updateLocation() {
        if (!getEntity().isDead()) {
            getEntity().teleport(decideLocation());
            calculateTriangle(getTerminal());
        }
    }

    @Override
    public void update() {
        if (!getEntity().isDead()) {
            getEntity().update();
            for (EntityTextDisplay entity : entities.values()) {
                style.sync(entity);
                entity.update();
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (EntityTextDisplay entity : entities.values()) {
            HologramAPI.getHologram().remove(entity);
        }
        entities.clear();
    }

    private static Vector3f toVector3f(float[] pos) {
        return new Vector3f(pos[0], pos[1], pos[2]);
    }

    protected void calculateTriangle(@NotNull Terminal<?> terminal) {
        float[][] pos = decidePos(terminal);
        if (pos == null) return;
        float[] p1 = pos[0], p2 = pos[1], p3 = pos[2];
        Location origin = new Location(terminal.getWorld(), p2[0], p2[1], p2[2]);
        TriangleUtils.textDisplayTriangle(
                toVector3f(p1), toVector3f(p2), toVector3f(p3)
        ).create(index -> {
            EntityTextDisplay entity = entities.get(index);
            if (entity != null) return entity;

            EntityTextDisplay newEntity = new EntityTextDisplay(getEntity().getRenderMode(), entityIdProvider);
            newEntity.setParent(getEntity());
            entities.put(index, newEntity);
            style.addEntity(newEntity);
            return newEntity;
        }, new Vector3f(p2[0], p2[1], p2[2]));
        for (EntityTextDisplay entity : entities.values()) {
            if (entity.isDead()) {
                HologramAPI.getHologram().spawn(entity, origin);
            } else {
                entity.teleport(origin);
                entity.update();
            }
        }
    }

    @Override
    public @NotNull Collection<AbstractEntity<?>> collectEntities() {
        return Lists.newArrayList(entities.values());
    }

    @Override
    protected EntityNone createHologram() {
        return new EntityNone(RenderMode.VIEWER_LIST);
    }

    @Override
    public void calculateSize() {
        this.width = 0;
        this.height = 0;
    }

    public float @Nullable [][] decidePos(Terminal<?> terminal) {
        if (pos1 == null || pos2 == null || pos3 == null || terminal == null) return null;
        double[] rawP1 = decideLocationRaw(pos1[0], pos1[1]);
        double[] rawP2 = decideLocationRaw(pos2[0], pos2[1]);
        double[] rawP3 = decideLocationRaw(pos3[0], pos3[1]);
        float[] p1 = terminal.getRotated(rawP1);
        float[] p2 = terminal.getRotated(rawP2);
        float[] p3 = terminal.getRotated(rawP3);
        return new float[][] { p1, p2, p3 };
    }

    public float @Nullable [] getPos1() {
        return pos1 == null ? null : new float[] { pos1[0], pos1[1] };
    }

    public Triangle setPos1(float @Nullable [] pos) {
        this.pos1 = pos == null ? null : new float[] { pos[0], pos[1] };
        return this;
    }

    public Triangle setPos1(float x, float y) {
        this.pos1 = new float[] { x, y };
        return this;
    }

    public float @Nullable [] getPos2() {
        return pos2 == null ? null : new float[] { pos2[0], pos2[1] };
    }

    public Triangle setPos2(float @Nullable [] pos) {
        this.pos2 = pos == null ? null : new float[] { pos[0], pos[1] };
        return this;
    }

    public Triangle setPos2(float x, float y) {
        this.pos2 = new float[] { x, y };
        return this;
    }

    public float @Nullable [] getPos3() {
        return pos3 == null ? null : new float[] { pos3[0], pos3[1] };
    }

    public Triangle setPos3(float @Nullable [] pos) {
        this.pos3 = pos == null ? null : new float[] { pos[0], pos[1] };
        return this;
    }

    public Triangle setPos3(float x, float y) {
        this.pos3 = new float[] { x, y };
        return this;
    }

    /**
     * 设置当有任意玩家的准心指向这个元素的状态更新时执行操作
     * @param hoverStateChange 悬停状态变化事件
     */
    public Triangle setOnHoverStateChange(HoverStateChange<Triangle> hoverStateChange) {
        this.hoverStateChange = hoverStateChange;
        hoverStateChange.perform(hoverState, this);
        return this;
    }

    @Override
    public void onTimerTick() {
        super.onTimerTick();

        tryUpdateHoverState(Hoverable.handleHover(getTerminal(), null, this));
    }

    @Override
    public void tryUpdateHoverState(boolean hover) {
        if (hover == hoverState) return;
        hoverState = hover;
        if (hoverStateChange != null) {
            hoverStateChange.perform(hover, this);
        }
    }

    @Override
    @Deprecated
    public double getX() {
        return super.getX();
    }
    @Override
    @Deprecated
    public Triangle setX(double x) {
        return super.setX(x);
    }
    @Override
    @Deprecated
    public double getY() {
        return super.getY();
    }
    @Override
    @Deprecated
    public Triangle setY(double y) {
        return super.setY(y);
    }
    @Override
    @Deprecated
    public Triangle setPos(double x, double y) {
        return super.setPos(x, y);
    }
    @Override
    @Deprecated
    public double getWidth() {
        return 0;
    }
    @Override
    @Deprecated
    public double getHeight() {
        return 0;
    }
    @Override
    @Deprecated
    public float getScaleX() {
        return super.getScaleX();
    }
    @Override
    @Deprecated
    public float getScaleY() {
        return super.getScaleY();
    }
    @Override
    @Deprecated
    public Triangle setScale(float scale) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setScale(float scaleX, float scaleY) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setScaleX(float scaleX) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setScaleY(float scaleY) {
        return this;
    }
    @Override
    @Deprecated
    public float @Nullable [] getAdditionalRotation() {
        return super.getAdditionalRotation();
    }
    @Override
    @Deprecated
    public Triangle setAdditionalRotation(float roll) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setAdditionalRotation(float yaw, float pitch) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setAdditionalRotation(float @Nullable [] additionalRotation) {
        return this;
    }
    @Override
    @Deprecated
    public Triangle setAdditionalRotation(float yaw, float pitch, float roll) {
        return this;
    }
}
