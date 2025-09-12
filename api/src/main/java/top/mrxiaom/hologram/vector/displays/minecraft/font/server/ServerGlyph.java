package top.mrxiaom.hologram.vector.displays.minecraft.font.server;

import top.mrxiaom.hologram.vector.displays.minecraft.font.api.Glyph;

public class ServerGlyph implements Glyph {
    private final float advance;
    public ServerGlyph(float advance) {
        this.advance = advance;
    }
    @Override
    public float getAdvance() {
        return advance;
    }
}
