package top.mrxiaom.hologram.vector.displays.config;

import org.bukkit.configuration.file.FileConfiguration;
import top.mrxiaom.hologram.vector.displays.VectorDisplays;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.IFontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.TextHandlerFactory;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;

public class FontConfig implements IConfig {
    private final VectorDisplays plugin;
    private final IFontManager manager;
    public FontConfig(VectorDisplays plugin) {
        TextHandlerFactory factory = NMS.getFactory();
        this.plugin = plugin;
        this.manager = new FontManager(factory);
        HologramFont.setTextRenderer(manager.createTextRenderer());
        plugin.getLogger().info("已加载服务端版本支持 " + factory.getName());
    }

    @Override
    public void reloadConfig(FileConfiguration config) {
        String path = config.getString("fonts-file", "font.json");
        File file = new File(plugin.getDataFolder(), path);
        try {
            if (!file.exists()) {
                InputStream resource = plugin.getResource("font.json");
                if (resource == null) {
                    throw new IllegalStateException("找不到资源文件 font.json");
                }
                manager.reload(resource);
            } else {
                manager.reload(file);
            }
            String scaleSample = config.getString("font-char-scale.sample-char", " ");
            double sampleCount = config.getDouble("font-char-scale.location-scale", 9.7407407407407407);
            HologramFont.setCharScale(scaleSample, sampleCount);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "重载字体时出现异常", t);
        }
        for (Font font : manager.getFonts()) {
            plugin.getLogger().info("已加载字体: " + font.getKey());
        }
    }
}
