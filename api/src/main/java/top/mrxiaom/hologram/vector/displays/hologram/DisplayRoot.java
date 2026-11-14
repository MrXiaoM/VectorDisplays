package top.mrxiaom.hologram.vector.displays.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.utils.joml.Matrix4f;

@ApiStatus.Internal
public interface DisplayRoot {
    @NotNull Player getPlayer();

    @NotNull Location getSpawnLocation();

    @NotNull Matrix4f transform(@NotNull EntityDisplay<?> entity);

    boolean isTranslationOnly();

    void onSpawn(@NotNull EntityDisplay<?> entity);

    void onRemove(@NotNull EntityDisplay<?> entity);
}
