package top.mrxiaom.hologram.vector.displays.hologram;

public enum RenderMode {
    /**
     * 不进行渲染
     */
    NONE,
    /**
     * 仅对处于 viewers 列表中的玩家进行渲染
     */
    VIEWER_LIST,
    /**
     * 对靠近这个虚拟实体的玩家进行渲染
     */
    NEARBY,
}
