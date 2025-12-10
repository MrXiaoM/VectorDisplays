package top.mrxiaom.hologram.vector.displays.minecraft.nms.mojmap.v1_21_R7;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * yarn 1.21.1: <code>net.minecraft.client.font.TextCollector</code>
 * <ul>
 *   <li><code>net.minecraft.text.StringVisitable</code> -> <code>net.minecraft.network.chat.IChatFormatted</code></li>
 * </ul>
 */
public class TextCollector {
    private final List<Component> texts = Lists.newArrayList();

    public TextCollector() {
    }

    public void add(Component text) {
        this.texts.add(text);
    }

    @Nullable
    public Component getRawCombined() {
        if (this.texts.isEmpty()) {
            return null;
        } else {
            if (this.texts.size() == 1) return this.texts.get(0);
            MutableComponent builder = texts.get(0).copy();
            for (int i = 1; i < this.texts.size(); i++) {
                builder.append(this.texts.get(i));
            }
            return builder;
        }
    }

    public Component getCombined() {
        Component stringVisitable = this.getRawCombined();
        return stringVisitable != null ? stringVisitable : Component.empty();
    }

    public void clear() {
        this.texts.clear();
    }
}
