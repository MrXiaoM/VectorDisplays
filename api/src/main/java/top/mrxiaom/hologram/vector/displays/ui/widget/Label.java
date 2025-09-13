package top.mrxiaom.hologram.vector.displays.ui.widget;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.utils.AdventureHelper;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;

public class Label extends Element<Label> {
    private @NotNull String text = "";
    public Label(@NotNull String id) {
        super(id);
    }
    public Label(@NotNull String id, @NotNull String text) {
        this(id);
        setText(text);
    }

    @NotNull
    public String getText() {
        return text;
    }

    public Label setText(@NotNull String text) {
        this.text = text;
        this.hologram.setText(AdventureHelper.miniMessage(text));
        this.calculateSize();
        return this;
    }

    @Override
    public void performClick(Player player, Action action) {
    }
}
