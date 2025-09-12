## 添加依赖


需要先添加依赖
```kotlin
// build.gradle(.kts)
dependencies {
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.21.0")
    implementation("top.mrxiaom.hologram:VectorDisplays-API:$VERSION")
    // TODO: 打包 packetevents 和 EntityLib
}
tasks {
    shadowJar {
        // 配置 relocation
        relocate("top.mrxiaom.hologram.vector.displays", "path.to.your.group")
    }
}
```
```yaml
# plugin.yml
# ...添加自动下载依赖声明
libraries:
  - "net.kyori:adventure-platform-bukkit:4.4.0"
  - "net.kyori:adventure-text-serializer-plain:4.21.0"
```


## 悬浮字管理

TODO

## TextRenderer 移植

编写管理类进行对接

```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.IFontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextRenderer;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.TextHandlerFactory;

import java.io.InputStream;
import java.util.logging.Level;

public class ServerFontManager {
    private static final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
    private final JavaPlugin plugin;
    private final IFontManager manager;
    private final ITextRenderer textRenderer;
    public ServerFontManager(JavaPlugin plugin) {
        this.plugin = plugin;
        TextHandlerFactory factory = NMS.getFactory();
        this.manager = new FontManager(factory);
        this.textRenderer = this.manager.createTextRenderer();
        this.reload();
    }

    public void reload() {
        try {
            // 加载字体文件 font.json
            InputStream resource = plugin.getResource("font.json");
            if (resource == null) {
                throw new IllegalStateException("找不到资源文件 font.json");
            }
            // manager.reload 支持传入 File，如有需要可以加载本地文件
            manager.reload(resource);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "重载字体时出现异常", t);
        }
        for (Font font : manager.getFonts()) {
            plugin.getLogger().info("已加载字体: " + font.getKey());
        }
    }
    /**
     * 获取文本宽度
     * @param text 文本
     */
    public double getWidth(String text) {
        int max = 0; // 找出最长的一行的文本宽度
        for (String s : text.split("\n")) {
            int width = textRenderer.getWidth(s);
            if (width > max) max = width;
        }
        return max;
    }

    /**
     * 获取文本宽度
     * @param text 文本
     */
    public double getWidth(Component text) {
        int max = 0; // 找出最长的一行的文本宽度
        for (String s : plainText.serialize(text).split("\n")) {
            int width = textRenderer.getWidth(s);
            if (width > max) max = width;
        }
        return max;
    }

    public ITextRenderer getTextRenderer() {
        return textRenderer;
    }
}
```
