package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_21_R3;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * yarn 1.21.1: <code>net.minecraft.client.font.TextCollector</code>
 * <ul>
 *   <li><code>net.minecraft.text.StringVisitable</code> -> <code>net.minecraft.network.chat.IChatFormatted</code></li>
 * </ul>
 */
public class TextCollector {
    private final List<IChatBaseComponent> texts = Lists.newArrayList();

    public TextCollector() {
    }

    public void add(IChatBaseComponent text) {
        this.texts.add(text);
    }

    @Nullable
    public IChatBaseComponent getRawCombined() {
        if (this.texts.isEmpty()) {
            return null;
        } else {
            if (this.texts.size() == 1) return this.texts.get(0);
            IChatMutableComponent builder = texts.get(0).f();
            for (int i = 1; i < this.texts.size(); i++) {
                builder.b(this.texts.get(i));
            }
            return builder;
        }
    }

    public IChatBaseComponent getCombined() {
        IChatBaseComponent stringVisitable = this.getRawCombined();
        return stringVisitable != null ? stringVisitable : IChatBaseComponent.i();
    }

    public void clear() {
        this.texts.clear();
    }
}
