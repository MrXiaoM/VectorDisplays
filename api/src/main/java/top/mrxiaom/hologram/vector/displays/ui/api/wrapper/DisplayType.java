package top.mrxiaom.hologram.vector.displays.ui.api.wrapper;

import me.tofaa.entitylib.meta.display.ItemDisplayMeta;

public enum DisplayType {
    NONE(ItemDisplayMeta.DisplayType.NONE),
    THIRD_PERSON_LEFT_HAND(ItemDisplayMeta.DisplayType.THIRD_PERSON_LEFT_HAND),
    THIRD_PERSON_RIGHT_HAND(ItemDisplayMeta.DisplayType.THIRD_PERSON_RIGHT_HAND),
    FIRST_PERSON_LEFT_HAND(ItemDisplayMeta.DisplayType.FIRST_PERSON_LEFT_HAND),
    FIRST_PERSON_RIGHT_HAND(ItemDisplayMeta.DisplayType.FIRST_PERSON_RIGHT_HAND),
    HEAD(ItemDisplayMeta.DisplayType.HEAD),
    GUI(ItemDisplayMeta.DisplayType.GUI),
    GROUND(ItemDisplayMeta.DisplayType.GROUND),
    FIXED(ItemDisplayMeta.DisplayType.FIXED),

    ;
    private final ItemDisplayMeta.DisplayType impl;
    DisplayType(ItemDisplayMeta.DisplayType impl) {
        this.impl = impl;
    }

    public ItemDisplayMeta.DisplayType toEntityLib() {
        return impl;
    }
}
