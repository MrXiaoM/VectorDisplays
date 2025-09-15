package top.mrxiaom.hologram.vector.displays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.PluginWrapper;
import top.mrxiaom.hologram.vector.displays.hologram.HologramAPI;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.utils.HologramUtils;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class TerminalManager implements Listener {
    private static TerminalManager instance = null;
    private final PluginWrapper plugin;
    private final HologramAPI hologramAPI;
    private final Map<String, Terminal<?>> terminals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private long timerPeriod = 1L;
    private IRunTask timerTask;
    public TerminalManager(PluginWrapper plugin) {
        if (instance != null) {
            throw new IllegalStateException("TerminalManager has been initialized!");
        }
        instance = this;
        this.plugin = plugin;
        this.hologramAPI = new HologramAPI(plugin);
    }

    public PluginWrapper getPlugin() {
        return plugin;
    }

    public void onLoad() {
        this.hologramAPI.onLoad();
    }

    public void onEnable() {
        this.hologramAPI.onEnable();
        this.plugin.registerEvents(this);
    }

    public long getTimerPeriod() {
        return timerPeriod;
    }

    public void setTimerPeriod(long timerPeriod) {
        if (timerPeriod == this.timerPeriod) return;
        this.timerPeriod = timerPeriod;
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timerTask = null;
        }
        this.timerTask = plugin.getScheduler().runTaskTimer(() -> {
            for (Terminal<?> terminal : terminals.values()) {
                terminal.onTimerTick();
            }
        }, 1L, timerPeriod);
    }

    public void onDisable() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timerTask = null;
        }
        for (Terminal<?> terminal : terminals.values()) {
            terminal.dispose();
        }
        terminals.clear();
        this.hologramAPI.onDisable();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = e.getPlayer();
        Action action = e.getAction();
        // 元素点击事件处理
        if (HologramUtils.isLeftClick(action) || HologramUtils.isRightClick(action)) {
            for (Terminal<?> terminal : terminals.values()) {
                if (!terminal.getViewers().contains(player)) continue;
                if (terminal.tryPerformClick(player, action)) {
                    e.setCancelled(true);
                    break; // 一次只允许点击一个界面
                }
            }
        }
    }

    /**
     * 获取所有已生成的终端面板
     */
    public Map<String, Terminal<?>> getTerminals() {
        return Collections.unmodifiableMap(terminals);
    }

    /**
     * 生成终端面板
     */
    public void spawn(Terminal<?> terminal) {
        Terminal<?> old = terminals.remove(terminal.getId());
        if (old != null) old.dispose();
        HologramAPI.getHologram().spawn(terminal.getHologram(), terminal.getLocation());
        terminal.init();
        terminals.put(terminal.getId(), terminal);
    }

    /**
     * 销毁终端面板
     */
    public void destroy(Terminal<?> terminal) {
        terminals.remove(terminal.getId());
        terminal.dispose();
    }

    /**
     * 销毁终端面板
     */
    public void destroy(String id) {
        Terminal<?> terminal = terminals.remove(id);
        if (terminal != null) {
            terminal.dispose();
        }
    }

    @NotNull
    public static TerminalManager inst() {
        return instance;
    }
}
