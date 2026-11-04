package top.mrxiaom.hologram.vector.displays.minecraft.font;

import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import top.mrxiaom.hologram.vector.displays.minecraft.font.api.ITextHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 不依赖 NMS 的文字宽度计算与换行实现
 */
public class AdventureTextHandler implements ITextHandler {
    private static final String[] LEGACY_COLORS = new String[] {
        "black", "dark_blue", "dark_green", "dark_aqua",
        "dark_red", "dark_purple", "gold", "gray",
        "dark_gray", "blue", "green", "aqua",
        "red", "light_purple", "yellow", "white"
    };

    @FunctionalInterface
    public interface WidthProvider {
        float getWidth(int codePoint, Style style);
    }

    private final WidthProvider widthProvider;
    private Function<String, String> translations = key -> null;

    public AdventureTextHandler(WidthProvider widthProvider) {
        this.widthProvider = widthProvider;
    }

    private static AdventureSerializer serializer() {
        return AdventureSerializer.serializer();
    }

    public void setTranslations(Function<String, String> translations) {
        this.translations = translations;
    }

    private record CharacterInfo(int codePoint, Style style, int start, int end, Component object) {

        CharacterInfo(int codePoint, Style style, int start, int end) {
            this(codePoint, style, start, end, null);
        }
    }

    private List<CharacterInfo> characters(Component text) {
        List<CharacterInfo> result = new ArrayList<>();
        collect(text, Style.empty(), result);
        return result;
    }

    private void collect(Component component, Style parent, List<CharacterInfo> result) {
        Style style = component.style().merge(parent, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        if (component instanceof TextComponent text) {
            collect(text.content(), style, result);
        } else if (component instanceof TranslatableComponent text) {
            String translation = translations.apply(text.key());
            if (translation == null) {
                translation = text.fallback();
            }
            if (translation == null) {
                translation = text.key();
            }
            int argument = 0;
            int literal = 0;
            for (int i = 0; i < translation.length(); i++) {
                if (translation.charAt(i) != '%' || i + 1 >= translation.length()) {
                    continue;
                }
                collect(translation.substring(literal, i), style, result);
                int end = i + 1;
                if (translation.charAt(end) == '%') {
                    collect("%", style, result);
                } else {
                    while (end < translation.length() && Character.isDigit(translation.charAt(end))) {
                        end++;
                    }
                    int index = argument;
                    boolean indexed = end > i + 1
                            && end < translation.length()
                            && translation.charAt(end) == '$';
                    if (indexed) {
                        index = Integer.parseInt(translation.substring(i + 1, end)) - 1;
                        end++;
                    }
                    if (end < translation.length() && translation.charAt(end) == 's') {
                        if (!indexed) {
                            argument++;
                        }
                        if (index >= 0 && index < text.args().size()) {
                            collect(text.args().get(index), style, result);
                        }
                    } else {
                        collect(translation.substring(i, end), style, result);
                        end--;
                    }
                }
                i = end;
                literal = end + 1;
            }
            collect(translation.substring(literal), style, result);
        } else if (component instanceof KeybindComponent text) {
            String translated = translations.apply(text.keybind());
            collect(translated == null ? text.keybind() : translated, style, result);
        } else if (component instanceof ScoreComponent text) {
            collect(text.value() == null ? "" : text.value(), style, result);
        } else if (component instanceof SelectorComponent text) {
            collect(text.pattern(), style, result);
        } else if (isObjectComponent(component)) {
            // object 默认占一个 8 像素方格
            result.add(new CharacterInfo(
                    0xFFFC, style, 0, 1, component.children(List.of()).style(style)));
        }
        for (Component child : component.children()) {
            collect(child, style, result);
        }
    }

    private static boolean isObjectComponent(Component component) {
        for (Class<?> type : component.getClass().getInterfaces()) {
            if (type.getName().equals("net.kyori.adventure.text.ObjectComponent")) {
                return true;
            }
        }
        return false;
    }

    private static void collect(String text, Style initial, List<CharacterInfo> result) {
        Style style = initial;
        for (int i = 0; i < text.length(); ) {
            int start = i;
            char ch = text.charAt(i++);
            if (ch == '\u00a7') {
                if (i == text.length()) {
                    break;
                }
                char code = Character.toLowerCase(text.charAt(i++));
                int color = "0123456789abcdef".indexOf(code);
                if (color >= 0) {
                    style = style.color(NamedTextColor.NAMES.value(LEGACY_COLORS[color]));
                    for (TextDecoration decoration : TextDecoration.values()) {
                        style = style.decoration(decoration, false);
                    }
                } else if (code == 'r') {
                    style = initial;
                } else {
                    TextDecoration decoration = switch (code) {
                        case 'k' -> TextDecoration.OBFUSCATED;
                        case 'l' -> TextDecoration.BOLD;
                        case 'm' -> TextDecoration.STRIKETHROUGH;
                        case 'n' -> TextDecoration.UNDERLINED;
                        case 'o' -> TextDecoration.ITALIC;
                        default -> null;
                    };
                    if (decoration != null) {
                        style = style.decoration(decoration, true);
                    }
                }
                continue;
            }
            int codePoint = ch;
            if (Character.isHighSurrogate(ch)
                    && i < text.length()
                    && Character.isLowSurrogate(text.charAt(i))) {
                codePoint = Character.toCodePoint(ch, text.charAt(i++));
            } else if (Character.isSurrogate(ch)) {
                codePoint = 0xFFFD;
            }
            result.add(new CharacterInfo(codePoint, style, start, i));
        }
    }

    private float width(CharacterInfo character) {
        if (character.codePoint == '\n') {
            return 0.0f;
        }
        if (character.object != null) {
            boolean bold = character.style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE;
            return 8.0f + (bold ? 1.0f : 0.0f);
        }
        return widthProvider.getWidth(character.codePoint, character.style);
    }

    private float width(List<CharacterInfo> characters) {
        float max = Float.NEGATIVE_INFINITY;
        float line = 0.0f;
        for (CharacterInfo character : characters) {
            if (character.codePoint == '\n') {
                max = Math.max(max, line);
                line = 0.0f;
            } else {
                line += width(character);
            }
        }
        return Math.max(max, line);
    }

    private List<CharacterInfo> characters(String text) {
        List<CharacterInfo> result = new ArrayList<>();
        if (text != null) {
            collect(text, Style.empty(), result);
        }
        return result;
    }

    public float getWidth(Component text) {
        return width(characters(text));
    }

    @Override
    public float getWidth(JsonElement json) {
        return getWidth(serializer().fromJsonTree(json));
    }

    @Override
    public float getWidth(String text) {
        return width(characters(text));
    }

    private static int lines(List<CharacterInfo> characters) {
        int lines = 1;
        for (CharacterInfo character : characters) {
            if (character.codePoint == '\n') {
                lines++;
            }
        }
        return lines;
    }

    public int getLines(Component text) {
        return lines(characters(text));
    }

    @Override
    public int getLines(JsonElement json) {
        return getLines(serializer().fromJsonTree(json));
    }

    @Override
    public int getLines(String text) {
        return lines(characters(text));
    }

    private static Component component(List<CharacterInfo> characters, int start, int end) {
        var result = Component.text();
        StringBuilder part = new StringBuilder();
        Style style = null;
        for (int i = start; i < end; i++) {
            CharacterInfo character = characters.get(i);
            if (character.object != null) {
                if (style != null) {
                    result.append(Component.text(part.toString()).style(style));
                }
                part.setLength(0);
                style = null;
                result.append(character.object);
                continue;
            }
            if (style != null && !style.equals(character.style)) {
                result.append(Component.text(part.toString()).style(style));
                part.setLength(0);
            }
            style = character.style;
            part.appendCodePoint(character.codePoint);
        }
        if (style != null) {
            result.append(Component.text(part.toString()).style(style));
        }
        return result.build();
    }

    public Component trimToWidth(Component text, int maxWidth) {
        List<CharacterInfo> characters = characters(text);
        float remaining = maxWidth;
        int end = 0;
        while (end < characters.size()) {
            remaining -= width(characters.get(end));
            if (remaining < 0) {
                break;
            }
            end++;
        }
        return component(characters, 0, end);
    }

    @Override
    public JsonElement trimToWidth(JsonElement json, int maxWidth) {
        return serializer().asJsonTree(trimToWidth(serializer().fromJsonTree(json), maxWidth));
    }

    @Override
    public String trimToWidth(String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        float remaining = maxWidth;
        int end = 0;
        for (CharacterInfo character : characters(text)) {
            remaining -= width(character);
            if (remaining < 0) {
                break;
            }
            end = character.end;
        }
        return text.substring(0, end);
    }

    @Override
    public String trimToWidthBackwards(String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        List<CharacterInfo> characters = characters(text);
        float remaining = maxWidth;
        int start = text.length();
        for (int i = characters.size() - 1; i >= 0; i--) {
            CharacterInfo character = characters.get(i);
            remaining -= width(character);
            if (remaining < 0) {
                break;
            }
            start = character.start;
        }
        return text.substring(start);
    }

    public List<Component> wrapLines(Component text, int maxWidth) {
        List<CharacterInfo> characters = characters(text);
        List<Component> lines = new ArrayList<>();
        int start = 0;
        boolean trailingNewLine = false;
        float limit = Math.max(1, maxWidth);
        while (start < characters.size()) {
            float current = 0.0f;
            boolean nonZero = false;
            int lastSpace = -1;
            int end = start;
            for (; end < characters.size(); end++) {
                CharacterInfo character = characters.get(end);
                if (character.codePoint == '\n') {
                    break;
                }
                if (character.codePoint == ' ') {
                    lastSpace = end;
                }
                float advance = width(character);
                current += advance;
                if (nonZero && current > limit) {
                    if (lastSpace >= start) {
                        end = lastSpace;
                    }
                    break;
                }
                nonZero |= advance != 0.0f;
            }
            lines.add(component(characters, start, end));
            trailingNewLine = end < characters.size() && characters.get(end).codePoint == '\n';
            start = end;
            if (start < characters.size()
                    && (characters.get(start).codePoint == '\n' || characters.get(start).codePoint == ' ')) {
                start++;
            }
        }
        if (trailingNewLine) {
            lines.add(Component.empty());
        }
        return lines;
    }

    @Override
    public List<JsonElement> wrapLines(JsonElement json, int maxWidth) {
        List<JsonElement> result = new ArrayList<>();
        for (Component line : wrapLines(serializer().fromJsonTree(json), maxWidth)) {
            result.add(serializer().asJsonTree(line));
        }
        return result;
    }

    @Override
    public int wrapLinesSize(JsonElement json, int maxWidth) {
        return wrapLines(serializer().fromJsonTree(json), maxWidth).size();
    }

    @Override
    public int wrapLinesSize(String text, int maxWidth) {
        return wrapLines(Component.text(text == null ? "" : text), maxWidth).size();
    }
}
