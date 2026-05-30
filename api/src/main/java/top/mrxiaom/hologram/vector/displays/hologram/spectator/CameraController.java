package top.mrxiaom.hologram.vector.displays.hologram.spectator;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.vector.displays.api.IRunTask;
import top.mrxiaom.hologram.vector.displays.api.IScheduler;
import top.mrxiaom.hologram.vector.displays.hologram.spectator.data.Node;
import top.mrxiaom.hologram.vector.displays.hologram.spectator.data.SmoothCurve;
import top.mrxiaom.hologram.vector.displays.utils.Bezier3;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 镜头平滑曲线配置
 */
public class CameraController {
    private final Map<Long, Node> nodes = new HashMap<>();
    private CameraController() {}

    public static CameraController create() {
        return new CameraController();
    }

    public static CameraController create(Collection<Node> nodes) {
        return create().addNodes(nodes);
    }

    public static CameraController create(Map<?, Node> nodes) {
        return create().addNodes(nodes.values());
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param origin 动画曲线原点位置
     * @param loc 路径点位置
     */
    public CameraController addNode(long timelineMills, Location origin, Location loc) {
        return addNode(Node.of(timelineMills, loc.clone().subtract(origin)));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param loc 路径点位置
     */
    public CameraController addNode(long timelineMills, Location loc) {
        return addNode(Node.of(timelineMills, loc));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param origin 动画曲线原点位置
     * @param loc 路径点位置
     * @param yaw 自定义镜头朝向（偏航角）
     * @param pitch 自定义镜头朝向（俯仰角）
     */
    public CameraController addNode(long timelineMills, Location origin, Location loc, float yaw, float pitch) {
        return addNode(Node.of(timelineMills, loc.clone().subtract(origin), yaw, pitch));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param loc 路径点位置
     * @param yaw 自定义镜头朝向（偏航角）
     * @param pitch 自定义镜头朝向（俯仰角）
     */
    public CameraController addNode(long timelineMills, Location loc, float yaw, float pitch) {
        return addNode(Node.of(timelineMills, loc, yaw, pitch));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param loc 路径点位置
     * @param vX 自定义镜头朝向（向量的 x 分量）
     * @param vY 自定义镜头朝向（向量的 y 分量）
     * @param vZ 自定义镜头朝向（向量的 z 分量）
     */
    public CameraController addNode(long timelineMills, Location loc, double vX, double vY, double vZ) {
        return addNode(Node.of(timelineMills, loc, vX, vY, vZ));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param x 路径点位置 x 坐标
     * @param y 路径点位置 y 坐标
     * @param z 路径点位置 z 坐标
     * @param yaw 自定义镜头朝向（偏航角）
     * @param pitch 自定义镜头朝向（俯仰角）
     */
    public CameraController addNode(long timelineMills, double x, double y, double z, float yaw, float pitch) {
        return addNode(Node.of(timelineMills, x, y, z, yaw, pitch));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param timelineMills 时间点（毫秒）
     * @param x 路径点位置 x 坐标
     * @param y 路径点位置 y 坐标
     * @param z 路径点位置 z 坐标
     * @param vX 自定义镜头朝向（向量的 x 分量）
     * @param vY 自定义镜头朝向（向量的 y 分量）
     * @param vZ 自定义镜头朝向（向量的 z 分量）
     */
    public CameraController addNode(long timelineMills, double x, double y, double z, double vX, double vY, double vZ) {
        return addNode(Node.of(timelineMills, x, y, z, vX, vY, vZ));
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param node 路径点
     */
    public CameraController addNode(@NotNull Node node) {
        this.nodes.put(node.timelineMills(), node);
        return this;
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param nodes 路径点集
     */
    public CameraController addNodes(@NotNull Node... nodes) {
        for (Node node : nodes) {
            this.nodes.put(node.timelineMills(), node);
        }
        return this;
    }

    /**
     * 为动画曲线添加一个相对路径点，其中 (0, 0) 是动画曲线的原点
     * @param nodes 路径点集
     */
    public CameraController addNodes(@NotNull Collection<Node> nodes) {
        for (Node node : nodes) {
            this.nodes.put(node.timelineMills(), node);
        }
        return this;
    }

    /**
     * 获取动画曲线上的一个路径点
     * @param timelineMills 路径点的准确时间点（毫秒）
     * @return 找不到路径点时返回 <code>null</code>
     */
    @Nullable
    public Node getNode(long timelineMills) {
        return this.nodes.get(timelineMills);
    }

    /**
     * 在动画曲线上移除一个路径点
     * @param timelineMills 路径点的准确时间点（毫秒）
     * @return 是否移除成功，即移除之前该路径点是否存在
     */
    public boolean removeNode(long timelineMills) {
        return this.nodes.remove(timelineMills) != null;
    }

    /**
     * 在动画曲线上移除一个路径点
     * @param node 路径点
     * @return 是否移除成功，即移除之前该路径点是否存在
     */
    public boolean removeNode(@NotNull Node node) {
        return this.nodes.remove(node.timelineMills(), node);
    }

    /**
     * 获取所有路径点
     */
    @NotNull
    public Map<Long, Node> getNodes() {
        return nodes;
    }

    /**
     * 创建一个新的动画机实例
     * @param origin 动画曲线的原点坐标
     */
    @NotNull
    public Animation createNew(@NotNull Location origin) {
        return new Animation(origin);
    }

    /**
     * 创建一个新的动画机实例
     * @param origin 动画曲线的原点坐标
     * @param customRotation 偏航角度 yaw，用于旋转动画曲线
     */
    @NotNull
    public Animation createNew(@NotNull Location origin, float customRotation) {
        return new Animation(origin, customRotation);
    }

    /**
     * 创建一个新的动画机实例
     * @param origin 动画曲线的原点坐标
     * @param face 用于转换为偏航角度 yaw，以旋转动画曲线
     */
    @NotNull
    public Animation createNew(@NotNull Location origin, BlockFace face) {
        int x = face.getModX();
        int z = face.getModZ();
        if (x == 0 && z == 0) {
            return new Animation(origin);
        } else {
            final double _2PI = 2 * Math.PI;
            float customRotation = (float) Math.toDegrees((Math.atan2(-x, z) + _2PI) % _2PI);
            return new Animation(origin, customRotation);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public class Animation {
        private @Nullable Long startTime = null;
        private @NotNull Location origin;
        private @NotNull SmoothCurve curve;
        private @Nullable BiFunction<Long, Long, Long> overrideTime;
        public Animation(@NotNull Location origin) {
            setOrigin(origin, origin.getYaw());
        }
        public Animation(@NotNull Location origin, float customRotation) {
            setOrigin(origin, customRotation);
        }

        /**
         * 获取动画曲线的原点坐标
         */
        @NotNull
        public Location getOrigin() {
            return origin;
        }

        /**
         * 设置动画曲线原点，并重新计算动画曲线
         * @param origin 原点坐标
         */
        public Animation setOrigin(@NotNull Location origin) {
            return setOrigin(origin, origin.getYaw());
        }

        /**
         * 设置动画曲线原点，并重新计算动画曲线
         * @param origin 原点坐标
         * @param customRotation 不为 <code>null</code>时，使用自定义偏航角度 yaw 来旋转动画曲线
         */
        public Animation setOrigin(@NotNull Location origin, Float customRotation) {
            this.origin = origin.clone();
            List<Node> list = new ArrayList<>();
            // 初始点需要与玩家视角相同
            float yaw = this.origin.getYaw();
            float pitch = this.origin.getPitch();
            list.add(Node.of(0, 0, 0, 0, yaw, pitch));
            if (customRotation == null) {
                list.addAll(nodes.values());
            } else {
                // 将其它点的位置和摄像机朝向，从世界坐标系转换为玩家坐标系
                // yaw % 360 == 0 时，玩家坐标系与世界坐标系的坐标轴方向完全相同
                for (Node node : nodes.values()) {
                    list.add(node.rotateYaw(customRotation));
                }
            }
            this.curve = new SmoothCurve(list);
            return this;
        }

        /**
         * 获取开始播放的时间戳，未开始播放时返回 <code>null</code>
         */
        @Nullable
        public Long getStartTime() {
            return startTime;
        }

        public Animation setOverrideTime(@Nullable BiFunction<Long, Long, Long> overrideTime) {
            this.overrideTime = overrideTime;
            return this;
        }

        public Animation setOverrideTime(@Nullable Bezier3 bezier3) {
            if (bezier3 == null) {
                this.overrideTime = null;
            } else {
                this.overrideTime = (currentTime, endTime) -> {
                    if (currentTime < 0L) return 0L;
                    if (currentTime > endTime) return currentTime;
                    double t = (double) currentTime / endTime;
                    return (long) (bezier3.getPointD(t)[1] * endTime);
                };
            }
            return this;
        }

        /**
         * 获取当前播放进度
         */
        public long getCurrentTimeMills() {
            if (startTime == null) {
                return 0L;
            }
            long currentTime = System.currentTimeMillis() - startTime;
            if (overrideTime != null) {
                return overrideTime.apply(currentTime, curve.lastNode().timelineMills());
            } else {
                return currentTime;
            }
        }

        /**
         * 开始播放动画，需要自行调用 {@link top.mrxiaom.hologram.vector.displays.hologram.spectator.CameraController.Animation#getCurrentLocation()} 来获取当前镜头位置并更新给相应实体
         */
        public Animation start() {
            startTime = System.currentTimeMillis();
            return this;
        }

        /**
         * 获取动画当前是否已开始播放
         */
        public boolean isStarted() {
            return startTime != null;
        }

        /**
         * 获取动画是否已播放完毕
         */
        public boolean isEnded() {
            return isStarted() && getCurrentTimeMills() >= curve.lastNode().timelineMills();
        }

        /**
         * @see SmoothCurve#evaluate(long)
         */
        public double[] evaluate(long progress) {
            return curve.evaluate(progress);
        }

        /**
         * @see SmoothCurve#evaluateDirection(long)
         */
        public double[] evaluateDirection(long progress) {
            return curve.evaluateDirection(progress);
        }

        /**
         * @see SmoothCurve#evaluatePosition(long)
         */
        public double[] evaluatePosition(long progress) {
            return curve.evaluatePosition(progress);
        }

        /**
         * @see SmoothCurve#firstNode()
         */
        @NotNull
        public Node firstNode() {
            return curve.firstNode();
        }

        /**
         * @see SmoothCurve#lastNode()
         */
        @NotNull
        public Node lastNode() {
            return curve.lastNode();
        }

        /**
         * 获取当前相机应该在的世界位置，以及视角方向
         * <p>
         * 即使动画未开始播放，或动画播放结束，都可以获取到正确的位置
         */
        @NotNull
        public Location getCurrentLocation() {
            long time = getCurrentTimeMills();
            double[] pos = curve.evaluate(time);
            Location loc = origin.clone().add(pos[0], pos[1], pos[2]);

            final double _2PI = 2 * Math.PI;
            final double x = pos[3];
            final double z = pos[5];

            if (x == 0 && z == 0) {
                loc.setPitch(pos[4] > 0 ? -90 : 90);
                return loc;
            }

            double theta = Math.atan2(-x, z);
            loc.setYaw((float) Math.toDegrees((theta + _2PI) % _2PI));

            double x2 = NumberConversions.square(x);
            double z2 = NumberConversions.square(z);
            double xz = Math.sqrt(x2 + z2);
            loc.setPitch((float) Math.toDegrees(Math.atan(-pos[4] / xz)));

            return loc;
        }

        @NotNull
        public AnimationTask runTaskTimer(@NotNull IScheduler scheduler, @NotNull Consumer<Location> updater) {
            return runTaskTimer(scheduler, 1L, 1L, updater, null);
        }

        @NotNull
        public AnimationTask runTaskTimer(@NotNull IScheduler scheduler, @NotNull Consumer<Location> updater, @Nullable Runnable onEnd) {
            return runTaskTimer(scheduler, 1L, 1L, updater, onEnd);
        }

        @NotNull
        public AnimationTask runTaskTimer(@NotNull IScheduler scheduler, long delay, long period, @NotNull Consumer<Location> updater) {
            return runTaskTimer(scheduler, delay, period, updater, null);
        }
        @NotNull
        public AnimationTask runTaskTimer(@NotNull IScheduler scheduler, long delay, long period, @NotNull Consumer<Location> updater, @Nullable Runnable onEnd) {
            return new AnimationTask(this, scheduler, delay, period, updater, onEnd);
        }
    }

    public static class AnimationTask implements Runnable {
        private final @NotNull Animation animation;
        private final @NotNull Consumer<Location> updater;
        private final @Nullable Runnable onEnd;
        private IRunTask task;
        private AnimationTask(@NotNull Animation animation, IScheduler scheduler, long delay, long period, @NotNull Consumer<Location> updater, @Nullable Runnable onEnd) {
            this.animation = animation;
            this.updater = updater;
            this.onEnd = onEnd;
            this.task = scheduler.runTaskTimer(this, delay, period);
        }
        @Override
        public void run() {
            updater.accept(animation.getCurrentLocation());
            if (animation.isStarted()) {
                if (animation.isEnded() && task != null) {
                    task.cancel();
                    task = null;
                    if (onEnd != null) {
                        onEnd.run();
                    }
                }
            } else {
                animation.start();
            }
        }
    }
}
