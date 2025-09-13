package top.mrxiaom.hologram.vector.displays.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;
import top.mrxiaom.hologram.vector.displays.hologram.utils.Vector3F;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class TextHologram {
    private static final LegacyComponentSerializer legacyText = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

    private final JavaPlugin plugin;

    private long updateTaskPeriod = 20L * 3;
    private double nearbyEntityScanningDistance = 40.0;
    private final String id;

    private int entityID;

    protected Component text = Component.text("");
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    private Display.Billboard billboard = Display.Billboard.CENTER;
    private int interpolationDurationRotation = 10;
    private int interpolationDurationTransformation = 10;
    private double viewRange = 1.0;
    private boolean shadow = true;
    private int maxLineWidth = 200;
    private int backgroundColor;
    private boolean seeThroughBlocks = false;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private byte textOpacity = (byte) -1;
    private int brightnessOverride = -1;
    private final RenderMode renderMode;
    private Location location;
    private final List<Player> viewers = new CopyOnWriteArrayList<>();
    private boolean dead = true;
    private BukkitTask task;

    public TextHologram(String id, RenderMode renderMode) {
        JavaPlugin plugin = HologramAPI.getHologram().getPlugin();
        if (plugin == null) {
            throw new IllegalStateException("HologramAPI is not initialized!");
        }
        this.plugin = plugin;
        this.renderMode = renderMode;
        validateId(id);
        this.id = id.toLowerCase();
        startRunnable();
    }

    public TextHologram(String id) {
        this(id, RenderMode.NEARBY);
    }

    private void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    private void startRunnable() {
        if (task != null) return;
        task = Bukkit.getServer().getScheduler().runTaskTimer(HologramAPI.getHologram().getPlugin(), this::updateAffectedPlayers, 20L, updateTaskPeriod);
    }

    /**
     * Use HologramManager#spawn(TextHologram.class, Location.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void spawn(Location location) {
        this.location = location;
        entityID = ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE);
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                entityID, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            updateAffectedPlayers();
            sendPacket(packet);
            this.dead = false;
            update();
        });
    }

    public TextHologram update() {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            updateAffectedPlayers();
            TextDisplayMeta meta = createMeta();
            sendPacket(meta.createPacket());
        });
        return this;
    }

    private TextDisplayMeta createMeta() {
        TextDisplayMeta meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, EntityTypes.TEXT_DISPLAY);
        meta.setText(getTextAsComponent());
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(toVector3f(this.translation));
        meta.setScale(toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setLineWidth(this.maxLineWidth);
        meta.setViewRange((float) this.viewRange);
        meta.setBackgroundColor(this.backgroundColor);
        meta.setTextOpacity(this.textOpacity);
        meta.setShadow(this.shadow);
        meta.setSeeThrough(this.seeThroughBlocks);
        meta.setBrightnessOverride(this.brightnessOverride);
        meta.setRightRotation(this.rightRotation);
        meta.setLeftRotation(this.leftRotation);
        applyAlignment(meta);
        return meta;
    }

    private void applyAlignment(TextDisplayMeta meta) {
        switch (this.alignment) {
            case LEFT -> meta.setAlignLeft(true);
            case RIGHT -> meta.setAlignRight(true);
        }
    }

    private com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

    /**
     * Use HologramManager#remove(TextHologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
    }

    public TextHologram teleport(Location location) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return this;
    }

    public TextHologram addAllViewers(List<Player> viewerList) {
        this.viewers.addAll(viewerList);
        return this;
    }

    public TextHologram addViewer(Player player) {
        this.viewers.add(player);
        return this;
    }

    public TextHologram removeViewer(Player player) {
        this.viewers.remove(player);
        return this;
    }

    public TextHologram removeAllViewers() {
        this.viewers.clear();
        return this;
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public TextHologram setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setLeftRotation(float[] rotation) {
        this.leftRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        return this;
    }

    public TextHologram setRightRotation(float[] rotation) {
        this.rightRotation = new Quaternion4f(rotation[0], rotation[1], rotation[2], rotation[3]);
        return this;
    }

    public float[] getLeftRotation() {
        Quaternion4f r = leftRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public float[] getRightRotation() {
        Quaternion4f r = rightRotation;
        return new float[] { r.getX(), r.getY(), r.getZ(), r.getW() };
    }

    public TextHologram setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return this;
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public TextHologram setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return this;
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return legacyText.serialize(text);
    }

    public String getTextWithoutColor() {
        return plainText.serialize(text);
    }

    public TextHologram setText(String text) {
        this.text = Component.text(replaceFontImages(text));
        return this;
    }

    public TextHologram setText(Component component) {
        this.text = component;
        return this;
    }

    private String replaceFontImages(String string) {
        return HologramAPI.getReplaceText().replace(string);
    }

    private void updateAffectedPlayers() {
        if (this.location == null) return;
        viewers.stream()
                .filter(player -> player.isOnline() && (player.getWorld() != this.location.getWorld() || player.getLocation().distance(this.location) > 20))
                .forEach(player -> {
                    WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
                    HologramAPI.getPlayerManager().sendPacket(player, packet);
                });

        if (this.renderMode == RenderMode.VIEWER_LIST) return;

        if (this.renderMode == RenderMode.ALL) {
            this.addAllViewers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        } else if (this.renderMode == RenderMode.NEARBY && this.location.getWorld() != null) {
            this.location.getWorld().getNearbyEntities(this.location, nearbyEntityScanningDistance, nearbyEntityScanningDistance, nearbyEntityScanningDistance)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(entity -> this.viewers.add((Player) entity));
        }
    }

    private void sendPacket(PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE) return;
        viewers.forEach(player -> HologramAPI.getPlayerManager().sendPacket(player, packet));
    }

    public String getId() {
        return id;
    }

    public long getUpdateTaskPeriod() {
        return updateTaskPeriod;
    }

    public double getNearbyEntityScanningDistance() {
        return nearbyEntityScanningDistance;
    }

    public int getEntityID() {
        return entityID;
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public Location getLocation() {
        return location;
    }

    public List<Player> getViewers() {
        return viewers;
    }

    public boolean isDead() {
        return dead;
    }

    public BukkitTask getTask() {
        return task;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public TextHologram setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public int getInterpolationDurationRotation() {
        return interpolationDurationRotation;
    }

    public TextHologram setInterpolationDurationRotation(int interpolationDurationRotation) {
        this.interpolationDurationRotation = interpolationDurationRotation;
        return this;
    }

    public int getInterpolationDurationTransformation() {
        return interpolationDurationTransformation;
    }

    public TextHologram setInterpolationDurationTransformation(int interpolationDurationTransformation) {
        this.interpolationDurationTransformation = interpolationDurationTransformation;
        return this;
    }

    public double getViewRange() {
        return viewRange;
    }

    public TextHologram setViewRange(double viewRange) {
        this.viewRange = viewRange;
        return this;
    }

    public boolean isShadow() {
        return shadow;
    }

    public TextHologram setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public int getMaxLineWidth() {
        return maxLineWidth;
    }

    public TextHologram setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public TextHologram setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public boolean isSeeThroughBlocks() {
        return seeThroughBlocks;
    }

    public TextHologram setSeeThroughBlocks(boolean seeThroughBlocks) {
        this.seeThroughBlocks = seeThroughBlocks;
        return this;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    public TextHologram setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public TextHologram setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

    public int getBrightnessOverride() {
        return brightnessOverride;
    }

    public TextHologram setBrightnessOverride(int brightnessOverride) {
        this.brightnessOverride = brightnessOverride;
        return this;
    }
}
