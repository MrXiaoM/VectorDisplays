package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.joml.Matrix4f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class NMS {
    private static NMSFactory nmsFactory;

    public static NMSFactory getFactory() {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int major = parse(version, 0, 1);
        int minor = parse(version, 1, 0);
        int patch = parse(version, 2, 0);
        if (major != 1 || minor < 19 || (minor == 19 && patch < 4)) { // 1.19.4 以下
            throw new IllegalStateException("当前版本不受支持 (小于 1.19.4 或大于 v1)");
        }
        String craft;
        if (minor < 20 || (minor == 20 && patch < 3)) { // 1.20.3 以下
            craft = "v1_19_R3";
        } else if (minor == 20 && (patch == 3 || patch == 4)) { // 1.20.3-1.20.4
            craft = "v1_20_R3";
        } else if (minor == 20 && (patch == 5 || patch == 6)) { // 1.20.5-1.20.6
            craft = "v1_20_R4";
        } else if (minor == 21 && (patch == 0 || patch == 1)) { // 1.21-1.21.1
            craft = "v1_21_R1";
        } else if (minor == 21 && (patch == 2 || patch == 3)) { // 1.21.2-1.21.3
            craft = "v1_21_R2";
        } else if (minor == 21 && patch == 4) { // 1.21.4
            craft = "v1_21_R3";
        } else if (minor == 21 && patch == 5) { // 1.21.5
            craft = "v1_21_R4";
        } else if (minor == 21 && patch <= 8) { // 1.21.6-1.21.8
            craft = "v1_21_R5";
        } else { // 1.21.9 及以上通用
            craft = "v1_21_R6";

            /*
             在这里标注一下 1.21.x NMS 的问题
             拿 1.21.5+ 做依赖，编译的代码没法在 1.21.4 上面跑
             怀疑是新版本发生了破坏性变更，导致方法签名改变，但是代码用法没变
            */

            if (minor > 21) {
                Logger.getLogger("VectorDisplays").warning("看起来你正在使用一个不受支持的未来版本，已尝试使用本插件所支持的最新版本，插件可能无法正常工作");
            }
        }
        String className = NMS.class.getPackageName() + "." + craft + ".Factory";
        try {
            Class<?> type = Class.forName(className);
            return nmsFactory = (NMSFactory) type.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("找不到版本支持类 " + className + "，这可能由插件开发者未使用 ':all' 包引起");
        } catch (Throwable e) {
            throw new IllegalStateException("当前版本不受支持", e);
        }
    }

    public static NMSFactory getLoadedFactory() {
        return nmsFactory;
    }

    public static int nextEntityId(EntityType type) {
        Integer id = nmsFactory == null ? null : nmsFactory.nextEntityId();
        if (id == null) {
            return new WrapperEntity(type).getEntityId();
        }
        return id;
    }

    public static Matrix4f textDisplayUnitSquare() {
        if (nmsFactory == null) {
            return new Matrix4f()
                    .translate(-0.1f + .5f, -0.5f + .5f, 0f)
                    .scale(8.0f, 4.0f, 1f);
        }
        return nmsFactory.textDisplayUnitSquare();
    }

    public static <T> void reloadFontsViaNBTFile(InputStream stream, BiFunction<Integer, Float, T> glyph, BiConsumer<NamespacedKey, Int2ObjectMap<T>> loaded) throws Exception {
        nmsFactory.reloadFontsViaNBTFile(stream, glyph, loaded);
    }

    private static int parse(String[] split, int index, int def) {
        if (index >= split.length) return def;
        try {
            return Integer.parseInt(split[index]);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
