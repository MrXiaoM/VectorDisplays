package top.mrxiaom.hologram.vector.displays.minecraft.font.server;

import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Glyph;

public class ServerGlyph implements Glyph {
    private final float advance;
    private final float boldOffset;

    public ServerGlyph(float advance) {
        this(advance, 1.0f);
    }

    public ServerGlyph(float advance, float boldOffset) {
        this.advance = advance;
        this.boldOffset = boldOffset;
    }

    @Override
    public float getAdvance() {
        return advance;
    }

    @Override
    public float getBoldOffset() {
        return boldOffset;
    }
}
