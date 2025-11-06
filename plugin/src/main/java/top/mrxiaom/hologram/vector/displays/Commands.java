package top.mrxiaom.hologram.vector.displays;

import com.google.common.collect.Lists;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (sender.isOp()) {
                list.add("reload");
            }
            return startsWith(list, args[0]);
        }
        return Collections.emptyList();
    }
    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        if (list.isEmpty()) return Collections.emptyList();
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
