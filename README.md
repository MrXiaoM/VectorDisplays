# VectorDisplays 矢量显示屏

Minecraft 世界终端用户界面解决方案

由于[文本展示实体](https://zh.minecraft.wiki/w/%E5%B1%95%E7%A4%BA%E5%AE%9E%E4%BD%93#%E5%8E%86%E5%8F%B2)在 `1.19.4` 加入到游戏中，这个依赖库/插件只支持 `1.19.4+` 的服务端与客户端。

## 安装

对于开发人员，详见 [API](/api/README.md) 文档。

对于使用者，你可以使用以下方法安装插件：
+ 到[爱发电](https://afdian.com/a/mrxiaom)进行购买
+ 克隆本仓库，自行构建

至少需要 Java 17 环境才能安装本插件，此外，本插件需要前置 [packetevents](https://modrinth.com/plugin/packetevents/versions?l=spigot) 才能正常运行。

## 简介

你可想象过各种科幻电影、游戏中，悬在空中的半透明控制面板？  
托[文本展示实体](https://zh.minecraft.wiki/w/%E5%B1%95%E7%A4%BA%E5%AE%9E%E4%BD%93)可以自由旋转的福，现在，你可以轻松创建这样的控制面板！且无需对客户端作出任何修改，不需要 Mod，甚至不需要资源包。

你可能会好奇这个依赖库/插件是怎么工作的。我们只是做了一点较为复杂的数学运算，让文本展示实体出现在它应该出现的地方，并应用正确的旋转变换。所有文本展示实体的朝向都是默认的（朝向正南方，即Z轴正方向），我们改变的是文本展示实体的右旋转。  
文本展示实体的旋转变换使用四元数，这使得我们可以将其旋转到**任意角度**。即使四元数表达起来并不直观，但这在 1.19 之前几乎是不敢想的。

你可以自由设定这个控制面板中拥有哪些元素，使用类似于 Swing 或 WinForm 的形式，但 Minecraft 中限制较大，没有办法做到与他们并肩的自由度。  
除了修改文本展示实体可以调整的背景颜色、文本组件、亮度、缩放等以外，还可以：
+ 监听准心悬停状态更改事件，让元素在被玩家准心悬停时更改背景颜色等等
+ 监听玩家点击事件，让元素在被玩家点击时执行自定义操作

## 快速开始 (开发者)

对于需要将 VectorDisplays 作为依赖库嵌入到自己的插件里的开发者，请先阅读 [API](/api/README.md) 文档。

如果只需要将 VectorDisplays 作为依赖插件，按以下步骤操作。首先添加依赖
```kotlin
repositories {
    mavenCentral()
}
dependencies {
    compileOnly("top.mrxiaom.hologram:VectorDisplays-API:$VERSION")
}
```
创建你的第一个控制面板
```java
void create(Player player) {
    Location eyeLocation = player.getEyeLocation().clone(); eyeLocation.setPitch(0);
    // 获取玩家面前 2 格远的位置
    Location loc = player.getLocation().clone().add(eyeLocation.getDirection().multiply(2));
    // 操作面板ID, 位置, 宽度(单位为空格数), 高度(单位为行数)
    SimpleTerminal terminal = new SimpleTerminal("test_" + player.getName(), loc, 9, 3);
    terminal.setRotation(eyeLocation.getYaw(), -30.0f); // 设置终端面板旋转
    // 添加按钮
    terminal.addElement(new Button("btn1"), btn -> {
        btn.setText("<#FF0000>测试");
        btn.setScale(0.25f); // 缩放尺寸
        btn.setAlign(EnumAlign.RIGHT_CENTER); // 位置对齐方式
        btn.setPos(-2, 0); // 相对位置
        btn.setFullBrightness(); // 设置固定亮度
        btn.setOnHoverStateChange(hoverBg(0x80000000, 0)); // 悬停更改背景颜色
        btn.setOnClick((whoClicked, action, e) -> { // 点击执行操作
            t(whoClicked, "你以 " + action + " 方式点击了按钮 btn1");
        });
    });
    // 添加线条
    terminal.addElement(this.line = new Line("line1"), line -> {
        line.setFullBrightness();
        line.setPos1(-5, -5);
        line.setPos2(5, 5);
        line.setAlign(EnumAlign.CENTER);
    });
    // 添加玩家到这个终端面板
    terminal.addViewer(player);
    // 注册并生成悬浮字
    TerminalManager.inst().spawn(terminal);
}
```

## 鸣谢

+ [FabricMC/yarn](https://github.com/FabricMC/yarn) 本项目 TextRenderer 实现参考
+ [HologramAPI 1.2.x](https://github.com/HologramLib/HologramLib) 悬浮字控制逻辑
+ [Tofaa2/EntityLib](https://github.com/Tofaa2/EntityLib) 虚拟实体控制逻辑
+ [retrooper/packetevents](https://github.com/retrooper/packetevents) 用于发包控制虚拟悬浮字
+ [豆包AI](https://doubao.com/) 复杂数学运算的原型代码参考
