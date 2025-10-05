package top.mrxiaom.hologram.vector.displays.minecraft.nms;

import org.bukkit.Bukkit;

public class NMS {

    public static TextHandlerFactory getFactory() {
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
        } else if (minor == 20 || (minor == 21 && patch <= 4)) { // 1.20.5-1.21.4
            craft = "v1_20_R4";
        } else { // 1.21.5 及以上通用
            craft = "v1_21_R4";
        }
        String className = NMS.class.getPackageName() + "." + craft + ".Factory";
        try {
            Class<?> type = Class.forName(className);
            return (TextHandlerFactory) type.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("找不到版本支持类 " + className + "，这可能由插件开发者未使用 ':all' 包引起");
        } catch (Throwable e) {
            throw new IllegalStateException("当前版本不受支持", e);
        }
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
