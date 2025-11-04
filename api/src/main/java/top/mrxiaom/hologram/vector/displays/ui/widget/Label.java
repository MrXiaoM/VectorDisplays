package top.mrxiaom.hologram.vector.displays.ui.widget;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.utils.AdventureHelper;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;

public class Label extends TextElement<Label> {
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
        if (!this.hologram.isDead()) {
            this.updateLocation();
        }
        return this;
    }
}
