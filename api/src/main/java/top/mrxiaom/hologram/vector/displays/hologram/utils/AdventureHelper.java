package top.mrxiaom.hologram.vector.displays.hologram.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AdventureHelper {
    private static Field resolversField;
    private static final Map<String, Consumer<Component>> tagImplMap = new HashMap<>() {{
        put("shadow", c -> c.style().shadowColor(ShadowColor.none()));
        put("font", c -> c.style().font(Key.key("default")));
        put("gradient", c -> c.style().color(TextColor.color(255, 255, 255)));
    }};
    private static final List<String> disabledTags = new ArrayList<>();
    private static final MiniMessage miniMessage;

    static {
        disabledTags.add("pride");
        tagImplMap.forEach((tag, type) -> {
            try {
                type.accept(Component.empty());
            } catch (LinkageError e) {
                disabledTags.add(tag);
            }
        });
        miniMessage = create();
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public static void remove(TagResolver.Builder builder, Iterable<String> tags) {
        try {
            if (resolversField == null) {
                resolversField = builder.getClass().getDeclaredField("resolvers");
                resolversField.setAccessible(true);
            }
            List<TagResolver> list = (List<TagResolver>) resolversField.get(builder);
            list.removeIf(it -> {
                for (String tag : tags) {
                    if (it.has(tag)) return true;
                }
                return false;
            });
        } catch (Throwable ignored) {
        }
    }

    private static MiniMessage create() {
        return MiniMessage.builder()
                .editTags(it -> remove(it, disabledTags))
                .preProcessor(AdventureHelper::legacyToMiniMessage)
                .postProcessor(it -> it.decoration(TextDecoration.ITALIC, false))
                .build();
    }

    public static MiniMessage miniMessage() {
        return miniMessage;
    }

    @NotNull
    public static Component miniMessage(String s) {
        return s == null
                ? Component.empty()
                : miniMessage.deserialize(s);
    }

    @NotNull
    public static String miniMessage(Component component) {
        return component == null
                ? ""
                : miniMessage.serialize(component);
    }

    @NotNull
    public static List<Component> miniMessage(List<String> list) {
        if (list == null) return new ArrayList<>();
        List<Component> components = new ArrayList<>();
        for (String s : list) {
            components.add(s == null
                    ? Component.empty()
                    : miniMessage.deserialize(s));
        }
        return components;
    }

    @NotNull
    public static List<String> miniMessage_(List<Component> components) {
        if (components == null) return new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (Component component : components) {
            list.add(component == null
                    ? ""
                    : miniMessage.serialize(component));
        }
        return list;
    }

    public static @NotNull String legacyToMiniMessage(@NotNull String legacy) {
        StringBuilder builder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isColorCode(chars[i])) {
                builder.append(chars[i]);
                continue;
            }
            if (i + 1 >= chars.length) {
                builder.append(chars[i]);
                continue;
            }
            switch (Character.toLowerCase(chars[i+1])) {
                case '0': builder.append("<black>"); break;
                case '1': builder.append("<dark_blue>"); break;
                case '2': builder.append("<dark_green>"); break;
                case '3': builder.append("<dark_aqua>"); break;
                case '4': builder.append("<dark_red>"); break;
                case '5': builder.append("<dark_purple>"); break;
                case '6': builder.append("<gold>"); break;
                case '7': builder.append("<gray>"); break;
                case '8': builder.append("<dark_gray>"); break;
                case '9': builder.append("<blue>"); break;
                case 'a': builder.append("<green>"); break;
                case 'b': builder.append("<aqua>"); break;
                case 'c': builder.append("<red>"); break;
                case 'd': builder.append("<light_purple>"); break;
                case 'e': builder.append("<yellow>"); break;
                case 'f': builder.append("<white>"); break;
                case 'r': builder.append("<reset><!i>"); break;
                case 'l': builder.append("<b>"); break;
                case 'm': builder.append("<st>"); break;
                case 'o': builder.append("<i>"); break;
                case 'n': builder.append("<u>"); break;
                case 'k': builder.append("<obf>"); break;
                case 'x': {
                    if (i + 13 >= chars.length
                            || !isColorCode(chars[i+2])
                            || !isColorCode(chars[i+4])
                            || !isColorCode(chars[i+6])
                            || !isColorCode(chars[i+8])
                            || !isColorCode(chars[i+10])
                            || !isColorCode(chars[i+12])) {
                        builder.append(chars[i]);
                        continue;
                    }
                    builder
                            .append("<#")
                            .append(chars[i+3])
                            .append(chars[i+5])
                            .append(chars[i+7])
                            .append(chars[i+9])
                            .append(chars[i+11])
                            .append(chars[i+13])
                            .append(">");
                    i += 12;
                    break;
                }
                case '#': {
                    if (i + 6 >= chars.length) {
                        builder.append(chars[i]);
                        continue;
                    }
                    builder
                            .append("<#")
                            .append(chars,i+1, 6)
                            .append(">");
                    i += 5;
                    break;
                }
                default: {
                    builder.append(chars[i]);
                    if (chars[i+1] == chars[i]) { // && 转义为 &
                        i++;
                    }
                    continue;
                }
            }
            i++;
        }
        return builder.toString();
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isColorCode(char c) {
        return c == '§' || c == '&';
    }
}
