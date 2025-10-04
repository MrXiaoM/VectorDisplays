package top.mrxiaom.hologram.vector.displays.ui.api;

import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.EntityItemDisplay;

public interface EntityItemDisplayWrapper<This> extends EntityDisplayWrapper<This> {
    @Override
    @NotNull EntityItemDisplay getHologram();

    default ItemStack getItemStack() {
        return getHologram().getItemStack();
    }

    default This setItemStack(ItemStack itemStack) {
        getHologram().setItemStack(itemStack);
        return $this();
    }

    default ItemDisplayMeta.DisplayType getDisplayType() {
        return getHologram().getDisplayType();
    }

    default This setDisplayType(ItemDisplayMeta.DisplayType displayType) {
        getHologram().setDisplayType(displayType);
        return $this();
    }
}
