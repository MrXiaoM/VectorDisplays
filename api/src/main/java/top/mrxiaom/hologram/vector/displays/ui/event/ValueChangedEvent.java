package top.mrxiaom.hologram.vector.displays.ui.event;

import top.mrxiaom.hologram.vector.displays.ui.api.Element;

@FunctionalInterface
public interface ValueChangedEvent<E extends Element<E, ?>, V> {
    void perform(V oldValue, V newValue, E element);
}
