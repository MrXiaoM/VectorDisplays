package top.mrxiaom.hologram.vector.displays.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IEyeLocationAdapter {
    IEyeLocationAdapter DEFAULT = Player::getEyeLocation;

    @NotNull
    Location getEyeLocation(@NotNull Player player);
}
