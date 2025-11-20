package top.mrxiaom.hologram.vector.displays.ui.api.style;

import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import top.mrxiaom.hologram.vector.displays.hologram.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityStyle<This extends EntityStyle<This, Entity>, Entity extends AbstractEntity<Entity>> {
    List<Entity> entities = new ArrayList<>();

    protected boolean glowing = false;
    protected boolean silent = true;
    protected EntityPose pose = EntityPose.STANDING;

    @SuppressWarnings("unchecked")
    protected This $this() {
        return (This) this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public This setGlowing(boolean glowing) {
        this.glowing = glowing;
        return $this();
    }

    public boolean isSilent() {
        return silent;
    }

    public This setSilent(boolean silent) {
        this.silent = silent;
        return $this();
    }

    public EntityPose getPose() {
        return pose;
    }

    public This setPose(EntityPose pose) {
        this.pose = pose;
        return $this();
    }

    public This addEntity(Entity entity) {
        entities.add(entity);
        this.sync(entity);
        return $this();
    }

    public This removeEntity(Entity entity) {
        entities.remove(entity);
        return $this();
    }

    public This removeEntity(int entityId) {
        entities.removeIf(entity -> entity.getEntityID() == entityId);
        return $this();
    }

    public void syncAll() {
        for (Entity entity : entities) {
            sync(entity);
        }
    }

    public void sync(Entity entity) {
        entity.setGlowing(glowing);
        entity.setSilent(silent);
        entity.setPose(pose);
    }
}
