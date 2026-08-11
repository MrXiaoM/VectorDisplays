package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerMeta {
    private final Player player;

    public PlayerMeta(Player player) {
        this.player = player;
    }

    public Player player() {
        return player;
    }

    @NotNull
    public PacketWrapper<?> processPacket(PacketWrapper<?> oldPacket) {
        return oldPacket;
    }
}
