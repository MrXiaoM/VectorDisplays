package top.mrxiaom.hologram.vector.displays.ui.api;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PageBuilder {
    private final Terminal<?> terminal;
    private final List<Element<?, ?>> elements = new ArrayList<>();
    public PageBuilder(Terminal<?> terminal) {
        this.terminal = terminal;
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param element 元素实例
     */
    public void addElement(Element<?, ?> element) {
        element.setTerminal(terminal);
        elements.add(element);
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param element 界面元素
     * @param consumer 额外参数
     */
    public <T extends Element<?, ?>> void addElement(T element, Consumer<T> consumer) {
        consumer.accept(element);
        addElement(element);
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param elements 元素实例
     */
    public void addElements(Element<?, ?>... elements) {
        for (Element<?, ?> element : elements) {
            addElement(element);
        }
    }

    /**
     * 向界面添加元素，建议在 <code>init()</code> 之前将元素添加完成
     * @param elements 元素实例
     */
    public void addElements(Collection<Element<?, ?>> elements) {
        if (elements == null) return;
        for (Element<?, ?> element : elements) {
            addElement(element);
        }
    }

    /**
     * 从界面中删除元素
     * @param id 元素ID
     * @return 被删除的元素实例
     */
    @Nullable
    public Element<?, ?> removeElement(String id) {
        for (Object o : elements.toArray()) {
            Element<?, ?> element = (Element<?, ?>) o;
            if (element.getId().equals(id) && removeElement(element)) {
                return element;
            }
        }
        return null;
    }

    /**
     * 从界面中删除元素
     * @param element 元素实例
     * @return 是否删除成功
     */
    public boolean removeElement(Element<?, ?> element) {
        return elements.remove(element);
    }

    /**
     * 获取已添加的元素列表
     */
    public List<Element<?, ?>> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
