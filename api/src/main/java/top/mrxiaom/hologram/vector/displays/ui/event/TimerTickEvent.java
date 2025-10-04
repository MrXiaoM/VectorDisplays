package top.mrxiaom.hologram.vector.displays.ui.event;

import top.mrxiaom.hologram.vector.displays.ui.api.Element;

@FunctionalInterface
public interface TimerTickEvent<E extends Element<E, ?>> {
    void tick(E element);
}
