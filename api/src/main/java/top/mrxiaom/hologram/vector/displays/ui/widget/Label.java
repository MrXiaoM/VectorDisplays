package top.mrxiaom.hologram.vector.displays.ui.widget;

import net.kyori.adventure.text.Component;
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

    /**
     * 获取悬浮字文本
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * 设置悬浮字文本，并重新计算悬浮字大小
     * @param text 支持 MiniMessage
     */
    public Label setText(@NotNull String text) {
        this.text = text;
        this.getEntity().setText(AdventureHelper.miniMessage(text));
        return postSetText();
    }

    /**
     * 设置悬浮字文本，并重新计算悬浮字大小
     * @param text 文本内容
     */
    public Label setText(@NotNull Component text) {
        this.text = AdventureHelper.miniMessage(text);
        this.getEntity().setText(text);
        return postSetText();
    }

    private Label postSetText() {
        this.calculateSize();
        if (!this.getEntity().isDead()) {
            this.updateLocation();
        }
        return this;
    }
}
