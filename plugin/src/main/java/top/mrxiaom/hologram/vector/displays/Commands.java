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

import java.util.*;
import java.util.function.Consumer;

public class Commands implements CommandExecutor, TabCompleter {
    private final VectorDisplays plugin;
    private SimpleTerminal terminal;
    private Label animatedLabel;
    protected Commands(VectorDisplays plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("vectordisplays");
        if (command == null) {
            throw new IllegalStateException("无法初始化命令 /vectordisplays");
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
        plugin.getScheduler().runTaskTimer(new Runnable() {
            private int index = 0;
            private boolean direction = false;
            @Override
            public void run() {
                if (animatedLabel == null) {
                    index = 0;
                    direction = false;
                } else {
                    String text = "这是一个测试用的 Animated Label 123";
                    int length = text.length();
                    animatedLabel.setText(Component.text(text.substring(0, index)));
                    animatedLabel.update();
                    if (direction) {
                        if (--index < 0) {
                            index = 0;
                            direction = false;
                        }
                    } else {
                        if (++index > length) {
                            index = length;
                            direction = true;
                        }
                    }
                }
            }
        }, 5L, 5L);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l, @NotNull String[] args) {
        if (args.length > 1 && "measure".equalsIgnoreCase(args[0]) && sender.hasPermission("vectordisplays.measure")) {
            StringJoiner joiner = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                joiner.add(args[i]);
            }
            sender.sendMessage("长度: " + HologramFont.getTextRenderer().getWidth(Component.text(joiner.toString())));
            return true;
        }
        if (args.length >= 1 && "test".equalsIgnoreCase(args[0]) && sender.hasPermission("vectordisplays.test")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("该命令只能由玩家执行");
                return true;
            }
            if (terminal != null) {
                animatedLabel = null;
                TerminalManager.inst().destroy(terminal);
                terminal = null;
                if (args.length == 1) {
                    sender.sendMessage("已移除测试面板");
                    return true;
                }
            }
            double terminalWidth;
            if (args.length > 1) {
                terminalWidth = Double.parseDouble(args[1]);
            } else {
                terminalWidth = 100.0;
            }
            Location eyeLocation = player.getEyeLocation().clone();
            eyeLocation.setPitch(0);
            Location loc = player.getLocation().clone().add(0, 0.75, 0).add(eyeLocation.getDirection().multiply(1.5));
            terminal = new SimpleTerminal(RenderMode.NEARBY, "vectordisplays_test", loc, terminalWidth, 50.0);
            terminal.setRotation(180.0f - eyeLocation.getYaw(), 0);
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
            terminal.addElement(new Label("left-top-1-alt"), label -> {
                leftTop.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("中");
                label.setText(Component.text(width));
                label.setPos(30, height * 1 * 0.25);
            });
            terminal.addElement(new Label("left-top-2"), label -> {
                leftTop.accept(label);
                label.setText(Component.text("中文"));
                label.setPos(0, height * 2 * 0.25);
            });
            terminal.addElement(new Label("left-top-2-alt"), label -> {
                leftTop.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("中文");
                label.setText(Component.text(width));
                label.setPos(30, height * 2 * 0.25);
            });
            terminal.addElement(new Label("left-top-3"), label -> {
                leftTop.accept(label);
                label.setText(Component.text("中文字"));
                label.setPos(0, height * 3 * 0.25);
            });
            terminal.addElement(new Label("left-top-3-alt"), label -> {
                leftTop.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("中文字");
                label.setText(Component.text(width));
                label.setPos(30, height * 3 * 0.25);
            });

            Consumer<Label> leftCenter = label -> {
                label.setAlign(EnumAlign.LEFT_CENTER);
                label.setBackgroundColor(0xFF00FFFF);
                label.setTextAlignment(TextDisplay.TextAlignment.LEFT);
                label.setScale(0.25f);
            };
            terminal.addElement(new Label("left-center"), label -> {
                leftCenter.accept(label);
                label.setText(space);
                label.setPos(0, 0);
            });
            terminal.addElement(animatedLabel = new Label("left-animated"), label -> {
                leftCenter.accept(label);
                label.setText(Component.empty());
                label.setPos(0, height * 0.25);
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
            terminal.addElement(new Label("right-bottom-1-alt"), label -> {
                rightBottom.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("English");
                label.setText(Component.text(width));
                label.setPos(-30, height * 3 * -0.25);
            });
            terminal.addElement(new Label("right-bottom-2"), label -> {
                rightBottom.accept(label);
                label.setText(Component.text("中文 English"));
                label.setPos(0, height * 2 * -0.25);
            });
            terminal.addElement(new Label("right-bottom-2-alt"), label -> {
                rightBottom.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("中文 English");
                label.setText(Component.text(width));
                label.setPos(-30, height * 2 * -0.25);
            });
            terminal.addElement(new Label("right-bottom-3"), label -> {
                rightBottom.accept(label);
                label.setText(Component.text("中 English 混合 " + HologramFont.getTextRenderer().getWidth(Component.space())));
                label.setPos(0, height * 1 * -0.25);
            });
            terminal.addElement(new Label("right-bottom-3-alt"), label -> {
                rightBottom.accept(label);
                int width = HologramFont.getTextRenderer().getWidth("中 English 混合 " + HologramFont.getTextRenderer().getWidth(Component.space()));
                label.setText(Component.text(width));
                label.setPos(-30, height * 1 * -0.25);
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
                label.setText(Component.text((int)terminal.getWidth() + "x" + (int)terminal.getHeight()));
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

            TerminalManager.inst().spawn(terminal);
            sender.sendMessage("已添加测试面板");
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

    private void add(CommandSender sender, List<String> list, String cmd) {
        if (sender.hasPermission("vectordisplays." + cmd)) {
            list.add(cmd);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            add(sender, list, "measure");
            add(sender, list, "test");
            add(sender, list, "reload");
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
