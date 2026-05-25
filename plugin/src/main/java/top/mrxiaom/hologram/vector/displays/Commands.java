package top.mrxiaom.hologram.vector.displays;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.EnumAlign;
import top.mrxiaom.hologram.vector.displays.ui.HologramFont;
import top.mrxiaom.hologram.vector.displays.ui.SimpleTerminal;
import top.mrxiaom.hologram.vector.displays.ui.widget.Label;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;
import top.mrxiaom.hologram.vector.displays.utils.TriangleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Commands implements CommandExecutor, TabCompleter {
    private final VectorDisplays plugin;
    private SimpleTerminal terminal;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l, @NotNull String[] args) {
        if (args.length == 1 && "test".equalsIgnoreCase(args[0]) && sender.hasPermission("vectordisplays.test")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("该命令只能由玩家执行");
                return true;
            }
            if (terminal != null) {
                TerminalManager.inst().destroy(terminal);
                terminal = null;
                sender.sendMessage("已移除测试面板");
            } else {
                Location eyeLocation = player.getEyeLocation().clone(); eyeLocation.setPitch(0);
                Location loc = player.getLocation().clone().add(0, 0.75, 0).add(eyeLocation.getDirection().multiply(1.5));
                terminal = new SimpleTerminal(RenderMode.NEARBY, "vectordisplays_test", loc, 100.0, 50.0);
                terminal.setRotation(180.0f - eyeLocation.getYaw(), -15.0f);
                double height = HologramUtils.LINE_HEIGHT;

                TextComponent space = Component.text("  ");
                Consumer<Label> leftTop = label -> {
                    label.setAlign(EnumAlign.LEFT_TOP);
                    label.setBackgroundColor(0xFFFF0000);
                    label.setTextAlignment(TextDisplay.TextAlignment.LEFT);
                    label.setScale(0.25f);
                };
                terminal.addElement(new Label("left-top"), label -> {
                    leftTop.accept(label);
                    label.setText(space);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("left-top-1"), label -> {
                    leftTop.accept(label);
                    label.setText(Component.text("中"));
                    label.setPos(0, height * 1 * 0.25);
                });
                terminal.addElement(new Label("left-top-2"), label -> {
                    leftTop.accept(label);
                    label.setText(Component.text("中文"));
                    label.setPos(0, height * 2 * 0.25);
                });
                terminal.addElement(new Label("left-top-3"), label -> {
                    leftTop.accept(label);
                    label.setText(Component.text("中文字"));
                    label.setPos(0, height * 3 * 0.25);
                });
                terminal.addElement(new Label("left-center"), label -> {
                    label.setAlign(EnumAlign.LEFT_CENTER);
                    label.setBackgroundColor(0xFF00FFFF);
                    label.setTextAlignment(TextDisplay.TextAlignment.LEFT);
                    label.setText(space);
                    label.setScale(0.25f);
                    label.setPos(0, 0);
                });
                Consumer<Label> leftBottom = label -> {
                    label.setAlign(EnumAlign.LEFT_BOTTOM);
                    label.setBackgroundColor(0xFFFF0000);
                    label.setTextAlignment(TextDisplay.TextAlignment.LEFT);
                    label.setScale(0.25f);
                };
                terminal.addElement(new Label("left-bottom"), label -> {
                    leftBottom.accept(label);
                    label.setText(space);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("left-bottom-1"), label -> {
                    leftBottom.accept(label);
                    label.setText(Component.text("English"));
                    label.setPos(0, height * 3 * -0.25);
                });
                terminal.addElement(new Label("left-bottom-2"), label -> {
                    leftBottom.accept(label);
                    label.setText(Component.text("中文 English"));
                    label.setPos(0, height * 2 * -0.25);
                });
                terminal.addElement(new Label("left-bottom-3"), label -> {
                    leftBottom.accept(label);
                    label.setText(Component.text("中 English 混合 " + HologramFont.getTextRenderer().getWidth(Component.space())));
                    label.setPos(0, height * 1 * -0.25);
                });

                Consumer<Label> rightTop = label -> {
                    label.setAlign(EnumAlign.RIGHT_TOP);
                    label.setBackgroundColor(0xFFFF0000);
                    label.setTextAlignment(TextDisplay.TextAlignment.RIGHT);
                    label.setScale(0.25f);
                };
                terminal.addElement(new Label("right-top"), label -> {
                    rightTop.accept(label);
                    label.setText(space);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("right-top-1"), label -> {
                    rightTop.accept(label);
                    label.setText(Component.text("中"));
                    label.setPos(0, height * 1 * 0.25);
                });
                terminal.addElement(new Label("right-top-2"), label -> {
                    rightTop.accept(label);
                    label.setText(Component.text("中文"));
                    label.setPos(0, height * 2 * 0.25);
                });
                terminal.addElement(new Label("right-top-3"), label -> {
                    rightTop.accept(label);
                    label.setText(Component.text("中文字"));
                    label.setPos(0, height * 3 * 0.25);
                });
                terminal.addElement(new Label("right-center"), label -> {
                    label.setAlign(EnumAlign.RIGHT_CENTER);
                    label.setBackgroundColor(0xFF00FFFF);
                    label.setTextAlignment(TextDisplay.TextAlignment.RIGHT);
                    label.setText(space);
                    label.setScale(0.25f);
                    label.setPos(0, 0);
                });
                Consumer<Label> rightBottom = label -> {
                    label.setAlign(EnumAlign.RIGHT_BOTTOM);
                    label.setBackgroundColor(0xFFFF0000);
                    label.setTextAlignment(TextDisplay.TextAlignment.RIGHT);
                    label.setScale(0.25f);
                };
                terminal.addElement(new Label("right-bottom"), label -> {
                    rightBottom.accept(label);
                    label.setText(space);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("right-bottom-1"), label -> {
                    rightBottom.accept(label);
                    label.setText(Component.text("English"));
                    label.setPos(0, height * 3 * -0.25);
                });
                terminal.addElement(new Label("right-bottom-2"), label -> {
                    rightBottom.accept(label);
                    label.setText(Component.text("中文 English"));
                    label.setPos(0, height * 2 * -0.25);
                });
                terminal.addElement(new Label("right-bottom-3"), label -> {
                    rightBottom.accept(label);
                    label.setText(Component.text("中 English 混合 " + HologramFont.getTextRenderer().getWidth(Component.space())));
                    label.setPos(0, height * 1 * -0.25);
                });

                terminal.addElement(new Label("center-top"), label -> {
                    label.setAlign(EnumAlign.CENTER_TOP);
                    label.setBackgroundColor(0xFF00FFFF);
                    label.setTextAlignment(TextDisplay.TextAlignment.CENTER);
                    label.setText(space);
                    label.setScale(0.25f);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("center"), label -> {
                    label.setAlign(EnumAlign.CENTER);
                    label.setBackgroundColor(0xFFFF00FF);
                    label.setTextAlignment(TextDisplay.TextAlignment.CENTER);
                    label.setText(Component.text("100x50"));
                    label.setScale(0.25f);
                    label.setPos(0, 0);
                });
                terminal.addElement(new Label("unit-square"), label -> {
                    Vector3f scale = TriangleUtils.textDisplayUnitSquare().getScale(new Vector3f());
                    label.setScale(scale.x() * 0.1f, scale.y() * 0.1f);
                    label.setBackgroundColor(0xFFFF00FF);
                    label.setText(Component.space());
                    label.setPos(0, 15);
                });
                terminal.addElement(new Label("center-bottom"), label -> {
                    label.setAlign(EnumAlign.CENTER_BOTTOM);
                    label.setBackgroundColor(0xFF00FFFF);
                    label.setTextAlignment(TextDisplay.TextAlignment.CENTER);
                    label.setText(space);
                    label.setScale(0.25f);
                    label.setPos(0, 0);
                });

                TerminalManager.inst().spawn(terminal);
                sender.sendMessage("已添加测试面板");
            }
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.hasPermission("vectordisplays.reload")) {
            if (terminal != null) {
                TerminalManager.inst().destroy(terminal);
                terminal = null;
            }
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
