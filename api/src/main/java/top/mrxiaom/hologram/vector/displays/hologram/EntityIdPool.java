package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 实体ID缓存池
 */
public class EntityIdPool implements IEntityIdProvider {
    private final List<Integer> cacheIds = new ArrayList<>();
    private final List<Integer> addedIds = new ArrayList<>();
    private final IEntityIdProvider defaultProvider;
    public EntityIdPool(IEntityIdProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }
    public EntityIdPool() {
        this(DEFAULT);
    }

    /**
     * 标记开始添加元素状态，即清空缓存无效标记
     */
    public void startAddElements() {
        addedIds.clear();
    }

    private int markAdded(int entityId) {
        // 标记为已添加，在下一轮添加元素之前，缓存无效
        addedIds.add(entityId);
        return entityId;
    }

    @Override
    public int nextId(@NotNull EntityType type) {
        synchronized (cacheIds) {
            Set<Integer> entityIds = HologramAPI.getHologram().getVirtualEntityIds();
            for (int id : cacheIds) {
                if (entityIds.contains(id)) continue; // 已使用的 Id 不进行使用
                if (addedIds.contains(id)) continue; // 已标记缓存无效的 Id 不进行使用

                // 如果有缓存，则返回缓存的 ID
                return markAdded(id);
            }
            // 如果没有缓存，则获取新的实体 ID
            int newId = defaultProvider.nextId(type);
            cacheIds.add(newId);
            return markAdded(newId);
        }
    }
}
