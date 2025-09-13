## 添加依赖

需要先添加依赖
```kotlin
// build.gradle(.kts)
repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://jitpack.io")
}
dependencies {
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.23.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.23.0")

    implementation("top.mrxiaom.hologram:VectorDisplays-API:$VERSION")
    implementation("com.github.Tofaa2.EntityLib:spigot:2.4.11")
    // packetevents 可选不打包，可以让用户自行添加前置插件
    implementation("com.github.retrooper:packetevents-spigot:2.9.5")
}
tasks {
    shadowJar {
        // 配置 relocation
        val target = "path.to.your.group"
        relocate("top.mrxiaom.hologram.vector.displays", "${target}.libs.displays")
        relocate("me.tofaa.entitylib", "${target}.libs.entitylib")
        // packetevents 可选不打包，可以让用户自行添加前置插件
        relocate("com.github.retrooper.packetevents", "${target}.libs.packetevents.api")
        relocate("io.github.retrooper.packetevents", "${target}.libs.packetevents.impl")
    }
}
```
```yaml
# plugin.yml
# ...添加自动下载依赖声明
libraries:
  - "net.kyori:adventure-platform-bukkit:4.4.1"
  - "net.kyori:adventure-text-serializer-plain:4.23.0"
  - "net.kyori:adventure-text-minimessage:4.23.0"
```


## 悬浮字管理

在插件主类添加以下内容
```java
public class ExamplePlugin extends JavaPlugin {
    // 暂不支持多实例，请勿创建多个实例
    private final TerminalManager terminalManager;
    public ExamplePlugin() {
        this.terminalManager = new PluginWrapper(this)
                // 可选，可自行实现 FoliaScheduler 替换上去
                .setScheduler(new BukkitScheduler(this))
                .createTerminalManager();
    }
    @Override
    public void onLoad() {
        this.terminalManager.onLoad();
    }
    
    public void onEnable() {
        // 最好在读取配置文件之前执行
        this.terminalManager.onEnable();
    }
    
    public void onDisable() {
        this.terminalManager.onEnable();
    }
}

```

在插件启用时，将 `TextRenderer` 传入 HologramFont 里面：
```java
void onEnable() {
    HologramFont.setTextRenderer(textRenderer);
    // 如果有需要，最好在重载配置时，把 charScale 也设置一下，默认值不一定准确
    FileConfiguration config = getConfig();
    String scaleSample = config.getString("font-char-scale.sample-char", " ");
    double sampleCount = config.getDouble("font-char-scale.location-scale", 9.7407407407407407);
    HologramFont.setCharScale(scaleSample, sampleCount);
}
```
对于创建 `TextRenderer` 的方法，详见下文。

## TextRenderer 移植

这是从 Minecraft 客户端移植过来的部分 TextRenderer 功能，主要用于处理文本长度。  
例如 `getWidth`、`wrapLines` 等等，以便精确计算文本框对齐、自动换行等。

以下示例简单编写管理类进行对接。

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
