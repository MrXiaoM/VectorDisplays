package top.mrxiaom.hologram.vector.displays.api;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.TerminalManager;

import java.io.InputStream;

/**
 * 插件包装
 */
public class PluginWrapper {
    private final @NotNull JavaPlugin plugin;
    private @NotNull IScheduler scheduler;
    public PluginWrapper(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = new BukkitScheduler(plugin);
    }

    /**
     * 获取插件主类
     */
    @NotNull
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * @see JavaPlugin#getResource(String)
     */
    @Nullable
    public InputStream getResource(String filename) {
        return plugin.getResource(filename);
    }

    public void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * 获取调度器实现
     */
    @NotNull
    public IScheduler getScheduler() {
        return scheduler;
    }

    /**
     * 设置调度器实现
     */
    public PluginWrapper setScheduler(@NotNull IScheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    /**
     * 创建 TerminalManager 示例
     */
    @NotNull
    public TerminalManager createTerminalManager() {
        return new TerminalManager(this);
    }
}
