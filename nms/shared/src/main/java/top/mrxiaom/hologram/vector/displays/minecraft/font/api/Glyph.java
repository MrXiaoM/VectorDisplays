package top.mrxiaom.hologram.vector.displays.minecraft.font.api;

public interface Glyph {
    float getAdvance();

    default float getAdvance(boolean bold) {
        return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0F);
    }

    default float getBoldOffset() {
        return 1.0F;
    }
}
