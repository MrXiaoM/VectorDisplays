package top.mrxiaom.hologram.vector.displays.ui.widget;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.hologram.IEntityIdProvider;
import top.mrxiaom.hologram.vector.displays.hologram.utils.AdventureHelper;
import top.mrxiaom.hologram.vector.displays.ui.api.TextElement;

public class Label extends TextElement<Label> {
    private @NotNull String text = "";
    public Label(@NotNull String id) {
        super(id);
    }
    public Label(@NotNull String id, @NotNull String text) {
        this(id, IEntityIdProvider.DEFAULT, text);
    }
    public Label(@NotNull String id, @NotNull IEntityIdProvider entityIdProvider) {
        super(id, entityIdProvider);
    }
    public Label(@NotNull String id, @NotNull IEntityIdProvider entityIdProvider, @NotNull String text) {
        this(id, entityIdProvider);
        setText(text);
    }

    @Override
    public void calculateSize() {
        calculateSize(true);
    }

    @NotNull
    public String getText() {
        return text;
    }

    public Label setText(@NotNull String text) {
        this.text = text;
        this.getEntity().setText(AdventureHelper.miniMessage(text));
        this.calculateSize();
        if (!this.getEntity().isDead()) {
            this.updateLocation();
        }
        return this;
    }
}
