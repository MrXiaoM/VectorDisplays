package top.mrxiaom.hologram.vector.displays.minecraft.font;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

class FontNbtReader {
    // 模仿 NbtAccounter.unlimitedHeap()，使用原版的 512 层嵌套深度。
    private static final int MAX_DEPTH = 512;

    private FontNbtReader() {
    }

    static Map<String, Object> read(InputStream stream) throws IOException {
        DataInputStream input = new DataInputStream(new GZIPInputStream(stream));
        if (input.readUnsignedByte() != 10) {
            throw new IOException("字体 NBT 的根标签必须是 Compound");
        }
        input.readUTF();
        return compound(input, 1);
    }

    private static Map<String, Object> compound(DataInputStream input, int depth) throws IOException {
        if (depth > MAX_DEPTH) {
            throw new IOException("字体 NBT 嵌套过深");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        while (true) {
            int type = input.readUnsignedByte();
            if (type == 0) {
                return result;
            }
            String name = input.readUTF();
            result.put(name, payload(input, type, depth));
        }
    }

    private static int length(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0) {
            throw new IOException("字体 NBT 数组长度无效：" + length);
        }
        return length;
    }

    private static Object payload(DataInputStream input, int type, int depth) throws IOException {
        switch (type) {
            case 1:
                return input.readByte();
            case 2:
                return input.readShort();
            case 3:
                return input.readInt();
            case 4:
                return input.readLong();
            case 5:
                return input.readFloat();
            case 6:
                return input.readDouble();
            case 7: {
                byte[] values = new byte[length(input)];
                input.readFully(values);
                return values;
            }
            case 8:
                return input.readUTF();
            case 9: {
                if (depth + 1 > MAX_DEPTH) {
                    throw new IOException("字体 NBT 嵌套过深");
                }
                int elementType = input.readUnsignedByte();
                int size = length(input);
                if (elementType > 12 || (elementType == 0 && size != 0)) {
                    throw new IOException("字体 NBT 列表类型无效");
                }
                List<Object> values = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    values.add(payload(input, elementType, depth + 1));
                }
                return values;
            }
            case 10:
                return compound(input, depth + 1);
            case 11: {
                int[] values = new int[length(input)];
                for (int i = 0; i < values.length; i++) {
                    values[i] = input.readInt();
                }
                return values;
            }
            case 12: {
                long[] values = new long[length(input)];
                for (int i = 0; i < values.length; i++) {
                    values[i] = input.readLong();
                }
                return values;
            }
            default:
                throw new IOException("不支持的字体 NBT 标签类型：" + type);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> compound(Object value) throws IOException {
        if (!(value instanceof Map<?, ?>)) {
            throw new IOException("字体 NBT 字段必须是 Compound");
        }
        return (Map<String, Object>) value;
    }

    static List<?> list(Map<String, Object> data, String name) throws IOException {
        Object value = data.get(name);
        if (!(value instanceof List<?>)) {
            throw new IOException("字体 NBT 缺少列表：" + name);
        }
        return (List<?>) value;
    }

    static String string(Map<String, Object> data, String name) throws IOException {
        Object value = data.get(name);
        if (!(value instanceof String)) {
            throw new IOException("字体 NBT 缺少字符串：" + name);
        }
        return (String) value;
    }

    static float number(Map<String, Object> data, String name, float fallback) throws IOException {
        Object value = data.get(name);
        if (value == null) {
            return fallback;
        }
        if (!(value instanceof Number)) {
            throw new IOException("字体 NBT 字段必须是数值：" + name);
        }
        return ((Number) value).floatValue();
    }
}
