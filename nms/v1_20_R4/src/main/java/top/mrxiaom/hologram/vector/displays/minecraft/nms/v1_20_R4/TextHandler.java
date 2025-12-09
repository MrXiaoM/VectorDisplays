package top.mrxiaom.hologram.vector.displays.minecraft.nms.v1_20_R4;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatFormatted;
import net.minecraft.util.FormattedString;
import net.minecraft.util.FormattedStringEmpty;
import net.minecraft.util.StringDecomposer;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.minecraft.nms.ITextHandler;
import top.mrxiaom.hologram.vector.displays.minecraft.utils.MutableFloat;
import top.mrxiaom.hologram.vector.displays.minecraft.utils.MutableInt;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * yarn 1.20.6: <code>net.minecraft.client.font.TextHandler</code>
 * <ul>
 *   <li><code>TextVisitFactory</code> -> <code>net.minecraft.util.StringDecomposer</code></li>
 *   <li><code>CharacterVisitor</code> -> <code>net.minecraft.util.FormattedStringEmpty</code></li>
 *   <li><code>OrderedText</code> -> <code>net.minecraft.util.FormattedString</code></li>
 *   <li><code>StringVisitable</code> -> <code>net.minecraft.network.chat.IChatFormatted</code></li>
 *   <li><code>Style</code> -> <code>net.minecraft.network.chat.ChatModifier</code></li>
 * </ul>
 */
public class TextHandler implements ITextHandler {
    final WidthRetriever widthRetriever;

    public TextHandler(WidthRetriever widthRetriever) {
        this.widthRetriever = widthRetriever;
    }

    @Override
    public int getLines(JsonElement json) {
        IChatBaseComponent text = fromJson(json);
        MutableInt mutableInt = new MutableInt(1);
        StringDecomposer.a(text, ChatModifier.a, (unused, style, codePoint) -> {
            if (codePoint == 0x000A) mutableInt.add(1);
            return true;
        });
        return mutableInt.intValue();
    }

    @Override
    public int getLines(@Nullable String text) {
        if (text == null) {
            return 1;
        }
        MutableInt mutableInt = new MutableInt(1);
        StringDecomposer.a(text, ChatModifier.a, (unused, style, codePoint) -> {
            if (codePoint == 0x000A) mutableInt.add(1);
            return true;
        });
        return mutableInt.intValue();
    }

    @Override
    public float getWidth(JsonElement json) {
        return getWidth(fromJson(json));
    }

    private boolean visit(int codePoint, ChatModifier style, MutableFloat mutableFloat) {
        if (codePoint == 0x000A) { // reset if it is '\n'
            mutableFloat.resetNextLine();
        } else {
            mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
        }
        return true;
    }

    public float getWidth(@Nullable String text) {
        if (text == null) {
            return 0.0F;
        } else {
            MutableFloat mutableFloat = new MutableFloat();
            StringDecomposer.a(text, ChatModifier.a, (unused, style, codePoint) -> visit(codePoint, style, mutableFloat));
            return mutableFloat.getMaxValue();
        }
    }

    @Override
    public JsonElement trimToWidth(JsonElement json, int maxWidth) {
        return toJson(trimToWidth(fromJson(json), maxWidth, ChatModifier.a));
    }

    @Override
    public String trimToWidth(String text, int maxWidth) {
        return trimToWidth(text, maxWidth, ChatModifier.a);
    }

    @Override
    public String trimToWidthBackwards(String text, int maxWidth) {
        return trimToWidthBackwards(text, maxWidth, ChatModifier.a);
    }

    @Override
    public List<JsonElement> wrapLines(JsonElement json, int maxWidth) {
        IChatBaseComponent formatted = fromJson(json);
        List<IChatBaseComponent> lines = wrapLines(formatted, maxWidth, ChatModifier.a);
        List<JsonElement> result = new ArrayList<>();
        for (IChatBaseComponent line : lines) {
            result.add(toJson(line));
        }
        return result;
    }

    @Override
    public int wrapLinesSize(JsonElement json, int maxWidth) {
        return wrapLines(fromJson(json), maxWidth, ChatModifier.a).size();
    }

    @Override
    public int wrapLinesSize(String text, int maxWidth) {
        return wrapLines(text, maxWidth, ChatModifier.a).size();
    }

    public float getWidth(IChatFormatted text) {
        MutableFloat mutableFloat = new MutableFloat();
        StringDecomposer.a(text, ChatModifier.a, (unused, style, codePoint) -> visit(codePoint, style, mutableFloat));
        return mutableFloat.getMaxValue();
    }

    public float getWidth(FormattedString text) {
        MutableFloat mutableFloat = new MutableFloat();
        text.accept((index, style, codePoint) -> visit(codePoint, style, mutableFloat));
        return mutableFloat.getMaxValue();
    }

    public int getTrimmedLength(String text, int maxWidth, ChatModifier style) {
        WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor((float)maxWidth);
        StringDecomposer.a(text, style, widthLimitingVisitor);
        return widthLimitingVisitor.getLength();
    }

    public String trimToWidth(String text, int maxWidth, ChatModifier style) {
        return text.substring(0, this.getTrimmedLength(text, maxWidth, style));
    }

    public String trimToWidthBackwards(String text, int maxWidth, ChatModifier style) {
        MutableFloat mutableFloat = new MutableFloat();
        MutableInt mutableInt = new MutableInt(text.length());
        StringDecomposer.b(text, style, (index, styleX, codePoint) -> {
            float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(codePoint, styleX));
            if (f > (float)maxWidth) {
                return false;
            } else {
                mutableInt.setValue(index);
                return true;
            }
        });
        return text.substring(mutableInt.intValue());
    }

    public IChatBaseComponent trimToWidth(IChatBaseComponent text, int width, ChatModifier style) {
        final WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor((float)width);
        return text.a(new IChatFormatted.b<IChatBaseComponent>() {
            private final TextCollector collector = new TextCollector();

            public Optional<IChatBaseComponent> accept(ChatModifier style, String string) {
                widthLimitingVisitor.resetLength();
                if (!StringDecomposer.a(string, style, widthLimitingVisitor)) {
                    String string2 = string.substring(0, widthLimitingVisitor.getLength());
                    if (!string2.isEmpty()) {
                        this.collector.add(IChatBaseComponent.b(string2).b(style));
                    }

                    return Optional.of(this.collector.getCombined());
                } else {
                    if (!string.isEmpty()) {
                        this.collector.add(IChatBaseComponent.b(string).b(style));
                    }

                    return Optional.empty();
                }
            }
        }, style).orElse(text);
    }

    public void wrapLines(String text, int maxWidth, ChatModifier style, boolean retainTrailingWordSplit, LineWrappingConsumer consumer) {
        int i = 0;
        int j = text.length();

        LineBreakingVisitor lineBreakingVisitor;
        for(ChatModifier style2 = style; i < j; style2 = lineBreakingVisitor.getEndingStyle()) {
            lineBreakingVisitor = new LineBreakingVisitor((float)maxWidth);
            boolean bl = StringDecomposer.a(text, i, style2, style, lineBreakingVisitor);
            if (bl) {
                consumer.accept(style2, i, j);
                break;
            }

            int k = lineBreakingVisitor.getEndingIndex();
            char c = text.charAt(k);
            int l = c != '\n' && c != ' ' ? k : k + 1;
            consumer.accept(style2, i, retainTrailingWordSplit ? l : k);
            i = l;
        }

    }

    public List<IChatBaseComponent> wrapLines(String text, int maxWidth, ChatModifier style) {
        List<IChatBaseComponent> list = Lists.newArrayList();
        this.wrapLines(text, maxWidth, style, false,
                (styleX, start, end) -> list.add(IChatBaseComponent.b(text.substring(start, end)).b(styleX)));
        return list;
    }

    public List<IChatBaseComponent> wrapLines(IChatBaseComponent text, int maxWidth, ChatModifier style) {
        List<IChatBaseComponent> list = Lists.newArrayList();
        this.wrapLines(text, maxWidth, style, (textX, lastLineWrapped) -> list.add(textX));
        return list;
    }

    public <T extends IChatBaseComponent> void wrapLines(T text, int maxWidth, ChatModifier style, BiConsumer<IChatBaseComponent, Boolean> lineConsumer) {
        List<StyledString> list = Lists.newArrayList();
        text.a((styleX, textX) -> {
            if (!textX.isEmpty()) {
                list.add(new StyledString(textX, styleX));
            }

            return Optional.empty();
        }, style);
        LineWrappingCollector lineWrappingCollector = new LineWrappingCollector(list);
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;

        while (bl) {
            bl = false;
            LineBreakingVisitor lineBreakingVisitor = new LineBreakingVisitor((float) maxWidth);

            for (StyledString styledString : lineWrappingCollector.parts) {
                boolean bl4 = StringDecomposer.a(styledString.literal, 0, styledString.style, style, lineBreakingVisitor);
                if (!bl4) {
                    int i = lineBreakingVisitor.getEndingIndex();
                    ChatModifier style2 = lineBreakingVisitor.getEndingStyle();
                    char c = lineWrappingCollector.charAt(i);
                    boolean bl5 = c == '\n';
                    boolean bl6 = bl5 || c == ' ';
                    bl2 = bl5;
                    IChatBaseComponent stringVisitable = lineWrappingCollector.collectLine(i, bl6 ? 1 : 0, style2);
                    lineConsumer.accept(stringVisitable, bl3);
                    bl3 = !bl5;
                    bl = true;
                    break;
                }

                lineBreakingVisitor.offset(styledString.literal.length());
            }
        }

        IChatBaseComponent stringVisitable2 = lineWrappingCollector.collectRemainders();
        if (stringVisitable2 != null) {
            lineConsumer.accept(stringVisitable2, bl3);
        } else if (bl2) {
            lineConsumer.accept(IChatBaseComponent.i(), false);
        }
    }

    public static IChatBaseComponent fromJson(JsonElement element) {
        return ComponentSerialization.a.decode(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new).getFirst();
    }

    public static JsonElement toJson(IChatBaseComponent text) {
        return ComponentSerialization.a.encodeStart(JsonOps.INSTANCE, text).getOrThrow(JsonParseException::new);
    }

    @FunctionalInterface
    public interface WidthRetriever {
        float getWidth(int codePoint, ChatModifier style);
    }

    private class WidthLimitingVisitor implements FormattedStringEmpty {
        private float widthLeft;
        private int length;

        public WidthLimitingVisitor(float maxWidth) {
            this.widthLeft = maxWidth;
        }

        public boolean accept(int i, ChatModifier style, int j) {
            this.widthLeft -= TextHandler.this.widthRetriever.getWidth(j, style);
            if (this.widthLeft >= 0.0F) {
                this.length = i + Character.charCount(j);
                return true;
            } else {
                return false;
            }
        }

        public int getLength() {
            return this.length;
        }

        public void resetLength() {
            this.length = 0;
        }
    }

    private class LineBreakingVisitor implements FormattedStringEmpty {
        private final float maxWidth;
        private int endIndex = -1;
        private ChatModifier endStyle;
        private boolean nonEmpty;
        private float totalWidth;
        private int lastSpaceBreak;
        private ChatModifier lastSpaceStyle;
        private int count;
        private int startOffset;

        public LineBreakingVisitor(float maxWidth) {
            this.endStyle = ChatModifier.a;
            this.lastSpaceBreak = -1;
            this.lastSpaceStyle = ChatModifier.a;
            this.maxWidth = Math.max(maxWidth, 1.0F);
        }

        public boolean accept(int i, ChatModifier style, int j) {
            int k = i + this.startOffset;
            switch (j) {
                case 10:
                    return this.breakLine(k, style);
                case 32:
                    this.lastSpaceBreak = k;
                    this.lastSpaceStyle = style;
                default:
                    float f = TextHandler.this.widthRetriever.getWidth(j, style);
                    this.totalWidth += f;
                    if (this.nonEmpty && this.totalWidth > this.maxWidth) {
                        return this.lastSpaceBreak != -1 ? this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle) : this.breakLine(k, style);
                    } else {
                        this.nonEmpty |= f != 0.0F;
                        this.count = k + Character.charCount(j);
                        return true;
                    }
            }
        }

        private boolean breakLine(int finishIndex, ChatModifier finishStyle) {
            this.endIndex = finishIndex;
            this.endStyle = finishStyle;
            return false;
        }

        private boolean hasLineBreak() {
            return this.endIndex != -1;
        }

        public int getEndingIndex() {
            return this.hasLineBreak() ? this.endIndex : this.count;
        }

        public ChatModifier getEndingStyle() {
            return this.endStyle;
        }

        public void offset(int extraOffset) {
            this.startOffset += extraOffset;
        }
    }

    @FunctionalInterface
    public interface LineWrappingConsumer {
        void accept(ChatModifier style, int start, int end);
    }

    private static class LineWrappingCollector {
        final List<StyledString> parts;
        private String joined;

        public LineWrappingCollector(List<StyledString> parts) {
            this.parts = parts;
            this.joined = parts.stream().map((part) -> part.literal).collect(Collectors.joining());
        }

        public char charAt(int index) {
            return this.joined.charAt(index);
        }

        public IChatBaseComponent collectLine(int lineLength, int skippedLength, ChatModifier style) {
            TextCollector textCollector = new TextCollector();
            ListIterator<StyledString> listIterator = this.parts.listIterator();
            int i = lineLength;
            boolean bl = false;

            while(listIterator.hasNext()) {
                StyledString styledString = listIterator.next();
                String string = styledString.literal;
                int j = string.length();
                String string2;
                if (!bl) {
                    if (i > j) {
                        textCollector.add(styledString.toText());
                        listIterator.remove();
                        i -= j;
                    } else {
                        string2 = string.substring(0, i);
                        if (!string2.isEmpty()) {
                            textCollector.add(styledString.toText(string2));
                        }

                        i += skippedLength;
                        bl = true;
                    }
                }

                if (bl) {
                    if (i <= j) {
                        string2 = string.substring(i);
                        if (string2.isEmpty()) {
                            listIterator.remove();
                        } else {
                            listIterator.set(new StyledString(string2, style));
                        }
                        break;
                    }

                    listIterator.remove();
                    i -= j;
                }
            }

            this.joined = this.joined.substring(lineLength + skippedLength);
            return textCollector.getCombined();
        }

        @Nullable
        public IChatBaseComponent collectRemainders() {
            TextCollector textCollector = new TextCollector();
            Objects.requireNonNull(textCollector);
            for (StyledString part : this.parts) {
                textCollector.add(part.toText());
            }
            this.parts.clear();
            return textCollector.getRawCombined();
        }
    }

    static class StyledString {
        final String literal;
        final ChatModifier style;

        public StyledString(String literal, ChatModifier style) {
            this.literal = literal;
            this.style = style;
        }

        public IChatBaseComponent toText() {
            return toText(literal);
        }

        public IChatBaseComponent toText(String anotherLiteral) {
            return IChatBaseComponent.b(anotherLiteral).b(style);
        }
    }
}
