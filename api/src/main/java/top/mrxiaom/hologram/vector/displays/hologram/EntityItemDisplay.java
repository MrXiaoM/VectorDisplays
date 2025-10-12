package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.ui.api.wrapper.DisplayType;

import java.util.Optional;
import java.util.UUID;

public class EntityItemDisplay extends EntityDisplay<EntityItemDisplay> {

    private ItemStack itemStack = null;
    private com.github.retrooper.packetevents.protocol.item.ItemStack itemAsPacket = com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY;
    private DisplayType displayType = DisplayType.NONE;

    public EntityItemDisplay(RenderMode renderMode) {
        super(renderMode);
        startRunnable();
    }

    public EntityItemDisplay() {
        this(RenderMode.NEARBY);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityTypes.ITEM_DISPLAY;
    }

    @Override
    public PacketWrapper<?> buildSpawnPacket() {
        return new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), getEntityType(),
                new Vector3d(location.getX(), location.getY(), location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
    }

    protected ItemDisplayMeta createMeta() {
        ItemDisplayMeta meta = (ItemDisplayMeta) EntityMeta.createMeta(this.entityID, getEntityType());
        applyDisplayMeta(meta);
        meta.setItem(itemAsPacket);
        meta.setDisplayType(displayType.toEntityLib());
        return meta;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public EntityItemDisplay setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemAsPacket = itemStack == null ? com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY : SpigotConversionUtil.fromBukkitItemStack(itemStack);
        return this;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public EntityItemDisplay setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
        return this;
    }
}
