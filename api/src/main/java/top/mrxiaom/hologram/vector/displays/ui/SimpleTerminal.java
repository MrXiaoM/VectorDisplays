package top.mrxiaom.hologram.vector.displays.ui;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;

public class SimpleTerminal extends Terminal {
    public SimpleTerminal(@NotNull String id, @NotNull Location location, int widthSpace, int heightLines) {
        super(id, location, widthSpace, heightLines);
    }
}
