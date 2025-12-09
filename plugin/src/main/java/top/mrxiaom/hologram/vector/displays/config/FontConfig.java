package top.mrxiaom.hologram.vector.displays.config;

import org.bukkit.configuration.file.FileConfiguration;
import top.mrxiaom.hologram.vector.displays.TerminalManager;
import top.mrxiaom.hologram.vector.displays.VectorDisplays;
import top.mrxiaom.hologram.vector.displays.minecraft.font.FontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Font;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.IFontManager;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMS;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.NMSFactory;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;

public class FontConfig implements IConfig {
    private final VectorDisplays plugin;
    private final IFontManager manager;
    private boolean forcesUnicodeFont;
    public FontConfig(VectorDisplays plugin) {
        NMSFactory factory = NMS.getFactory();
        this.plugin = plugin;
        this.manager = new FontManager(factory);
        HologramFont.setTextRenderer(manager.createTextRenderer());
        plugin.getLogger().info("已加载服务端版本支持 " + factory.getName());
    }

    @Override
    public void reloadConfig(FileConfiguration config) {
        long timerPeriod = config.getLong("timer-period", 1L);
        TerminalManager.inst().setTimerPeriod(Math.max(1L, timerPeriod));

        HologramUtils.LINE_HEIGHT = config.getDouble("line-height", 13);

        String path = config.getString("fonts-file", "font.json");
        File file = new File(plugin.getDataFolder(), path);
        try {
            if (!file.exists()) {
                InputStream resource = plugin.getResource("font.json");
                if (resource == null) {
                    throw new IllegalStateException("找不到资源文件 font.json");
                }
                this.manager.reload(resource);
            } else {
                this.manager.reload(file);
            }
            String scaleSample = config.getString("font-char-scale.sample-char", " ");
            double sampleCount = config.getDouble("font-char-scale.location-scale", 9.7407407407407407);
            this.forcesUnicodeFont = config.getBoolean("forces-unicode-font", false);
            this.manager.setForcesUnicodeFont(forcesUnicodeFont);
            HologramFont.setCharScale(scaleSample, sampleCount);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "重载字体时出现异常", t);
        }
        for (Font font : this.manager.getFonts()) {
            plugin.getLogger().info("已加载字体: " + font.getKey());
        }
    }
}
