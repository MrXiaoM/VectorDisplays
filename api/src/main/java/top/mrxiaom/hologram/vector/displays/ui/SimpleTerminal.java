package top.mrxiaom.hologram.vector.displays.ui;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;

public class SimpleTerminal extends Terminal<SimpleTerminal> {
    public SimpleTerminal(@NotNull String id, @NotNull Location location, int widthSpace, int heightLines) {
        super(id, location, widthSpace, heightLines);
    }
    public SimpleTerminal(@NotNull RenderMode renderMode, @NotNull String id, @NotNull Location location, int widthSpace, int heightLines) {
        super(renderMode, id, location, widthSpace, heightLines);
    }
    public SimpleTerminal(@NotNull String id, @NotNull Location location, double width, double height) {
        super(id, location, width, height);
    }
    public SimpleTerminal(@NotNull RenderMode renderMode, @NotNull String id, @NotNull Location location, double width, double height) {
        super(renderMode, id, location, width, height);
    }
}
