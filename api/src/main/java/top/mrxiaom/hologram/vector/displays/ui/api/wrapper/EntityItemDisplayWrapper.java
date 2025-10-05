package top.mrxiaom.hologram.vector.displays.ui.api.wrapper;

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

    default DisplayType getDisplayType() {
        return getHologram().getDisplayType();
    }

    default This setDisplayType(DisplayType displayType) {
        getHologram().setDisplayType(displayType);
        return $this();
    }
}
