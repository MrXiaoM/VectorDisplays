package top.mrxiaom.hologram.vector.displays;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    private final VectorDisplays plugin;
    protected Commands(VectorDisplays plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("vectordisplays");
        if (command == null) {
            throw new IllegalStateException("无法初始化命令 /vectordisplays");
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            sender.sendMessage("配置文件已重载");
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
