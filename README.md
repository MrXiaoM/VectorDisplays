<div align="center">
  <img width="100%" src="https://pic1.imgdb.cn/item/690709633203f7be00c2db18.webp" alt="header"/>

  <h1>VectorDisplays 矢量显示屏</h1>

  Minecraft 世界终端用户界面解决方案

  由于[文本展示实体](https://zh.minecraft.wiki/w/%E5%B1%95%E7%A4%BA%E5%AE%9E%E4%BD%93#%E5%8E%86%E5%8F%B2)在 `1.19.4` 加入到游戏中，这个依赖库/插件只支持 `1.19.4+` 的服务端与客户端。
</div>

## 安装

对于开发人员，详见本文下方的**快速开始**示例。

对于使用者，你首先需要满足以下条件
+ 服务端版本在 `1.19.4` 或以上，可使用 Spigot/Paper/Folia 服务端。
+ 至少使用 `Java 17` 运行服务端
+ 安装前置插件 [packetevents](https://modrinth.com/plugin/packetevents/versions?l=spigot)

然后，你可以使用以下**任意一种**方法获取插件：
+ 到[爱发电](https://afdian.com/p/9cf5cd80a9b511f0b7d05254001e7c00)进行购买
+ 通过[QQ](https://qm.qq.com/q/umnukgs1k4)或者[QQ群](https://qm.qq.com/q/bIu01ENOIU)联系作者进行购买
+ 克隆本仓库，自行构建  
  > CodeMC 仓库在中国大陆境内的速度极慢，建议自备代理。


> [!WARNING]
> 
> 任何第三方构建都是不可信的，为了您的安全，即使您不想购买，也请自行构建插件。  
> 人话：提醒到这个份上了，遇到了后门别找我。
> 

## 简介

你可想象过各种科幻电影、游戏中，悬在空中的半透明控制面板？  
托[文本展示实体](https://zh.minecraft.wiki/w/%E5%B1%95%E7%A4%BA%E5%AE%9E%E4%BD%93)可以自由旋转的福，现在，你可以轻松创建这样的控制面板！且无需对客户端作出任何修改，不需要 Mod，甚至不需要资源包。

示例插件 [VectorLoginUI](https://github.com/MrXiaoM/VectorLoginUI):  
![preview](https://pic1.imgdb.cn/item/6907046f3203f7be00c2d59b.webp)

你可能会好奇这个依赖库/插件是怎么工作的。我们只是做了一点较为复杂的数学运算，让文本展示实体出现在它应该出现的地方，并应用正确的旋转变换。所有文本展示实体的朝向都是默认的（朝向正南方，即Z轴正方向），我们改变的是文本展示实体的左旋转。  
文本展示实体的旋转变换使用四元数，这使得我们可以将其旋转到**任意角度**。即使四元数表达起来并不直观，但这在 1.19 之前几乎是不敢想的。

你可以自由设定这个控制面板中拥有哪些元素，使用类似于 Swing 或 WinForm 的形式，但 Minecraft 中限制较大，没有办法做到与他们并肩的自由度。  
除了修改文本展示实体可以调整的背景颜色、文本组件、亮度、缩放等以外，还可以：
+ 监听准心悬停状态更改事件，让元素在被玩家准心悬停时更改背景颜色等等
+ 监听玩家点击事件，让元素在被玩家点击时执行自定义操作

## 快速开始 (开发者)

请参阅 [MCIO Plugins](https://plugins.mcio.dev/elopers/vd/intro) 上的开发文档。

## 鸣谢

+ [FabricMC/yarn](https://github.com/FabricMC/yarn) 本项目 TextRenderer 实现参考
+ [HologramAPI 1.2.x](https://github.com/HologramLib/HologramLib) 悬浮字控制逻辑
+ [Tofaa2/EntityLib](https://github.com/Tofaa2/EntityLib) 虚拟实体控制逻辑
+ [retrooper/packetevents](https://github.com/retrooper/packetevents) 用于发包控制虚拟悬浮字
+ [TheCymaera](https://github.com/TheCymaera/minecraft-hologram) 提供三角形渲染方案
+ [豆包AI](https://doubao.com/) 复杂数学运算的原型代码参考
