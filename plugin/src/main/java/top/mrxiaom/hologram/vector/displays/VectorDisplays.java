package top.mrxiaom.hologram.vector.displays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.hologram.vector.displays.config.FontConfig;
import top.mrxiaom.hologram.vector.displays.config.IConfig;

import java.util.ArrayList;
import java.util.List;

public class VectorDisplays extends JavaPlugin {
    private final TerminalManager manager = new TerminalManager(this);
    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void onLoad() {
        manager.onLoad();
    }

    @Override
    public void onEnable() {
        manager.onEnable();
        configList.add(new FontConfig(this));
        new Commands(this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        FileConfiguration config = getConfig();
        for (IConfig inst : configList) {
            inst.reloadConfig(config);
        }
    }

    @Override
    public void onDisable() {
        manager.onDisable();
    }
}
