package top.mrxiaom.hologram.vector.displays.utils;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HologramUtilsBillboardTest {
    private static final float DELTA = 1.0E-5F;
    private static final double DOUBLE_DELTA = 1.0E-6D;

    @Test
    void fixedBillboardKeepsExistingRotation() {
        Location eyeLocation = eye(35.0F, -20.0F);
        float[] existingRotation = QuaternionUtils.fromEulerYXZtoQuaternion(20.0F, 10.0F, -5.0F);

        assertArrayEquals(new float[] { 0.0F, 0.0F, 0.0F, 1.0F },
                HologramUtils.getBillboardRotation(Display.Billboard.FIXED, eyeLocation), DELTA);
        assertArrayEquals(existingRotation,
                HologramUtils.getEffectiveRotation(existingRotation, Display.Billboard.FIXED, eyeLocation), DELTA);
    }

    @Test
    void verticalBillboardUsesOnlyCameraYaw() {
        float[] level = HologramUtils.getBillboardRotation(Display.Billboard.VERTICAL, eye(90.0F, 0.0F));
        float[] lookingUp = HologramUtils.getBillboardRotation(Display.Billboard.VERTICAL, eye(90.0F, -70.0F));

        assertArrayEquals(level, lookingUp, DELTA);
    }

    @Test
    void horizontalBillboardUsesOnlyCameraPitch() {
        float[] north = HologramUtils.getBillboardRotation(Display.Billboard.HORIZONTAL, eye(0.0F, 30.0F));
        float[] east = HologramUtils.getBillboardRotation(Display.Billboard.HORIZONTAL, eye(90.0F, 30.0F));

        assertArrayEquals(north, east, DELTA);
    }

    @Test
    void centerBillboardUsesBothCameraAxes() {
        float[] original = HologramUtils.getBillboardRotation(Display.Billboard.CENTER, eye(0.0F, 0.0F));
        float[] changedYaw = HologramUtils.getBillboardRotation(Display.Billboard.CENTER, eye(90.0F, 0.0F));
        float[] changedPitch = HologramUtils.getBillboardRotation(Display.Billboard.CENTER, eye(0.0F, 30.0F));

        assertQuaternionDiffers(original, changedYaw);
        assertQuaternionDiffers(original, changedPitch);
    }

    @Test
    void inverseBillboardRotationRestoresOriginalPointForEveryMode() {
        Location origin = new Location(null, 12.0D, 64.0D, -8.0D);
        Location point = new Location(null, 14.0D, 65.5D, -5.0D);
        Location eyeLocation = eye(42.0F, -25.0F);

        for (Display.Billboard billboard : Display.Billboard.values()) {
            float[] rotation = HologramUtils.getBillboardRotation(billboard, eyeLocation);
            Location rotated = QuaternionUtils.rotateChildren(origin, rotation, point);
            Location restored = QuaternionUtils.rotateChildren(origin, conjugate(rotation), rotated);

            assertLocationEquals(point, restored);
        }
    }

    @Test
    void verticalBillboardRaytraceUsesVisibleCameraFacingPlane() {
        Location eyeLocation = new Location(null, 5.0D, 0.5D, 0.0D, 90.0F, 0.0F);
        Location anchor = new Location(null, 0.0D, 0.0D, 0.0D);
        float[] identity = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };

        assertNull(HologramUtils.raytraceElement(
                identity, null, Display.Billboard.FIXED, anchor, 0.0D, 0.0D, 2.0D, 1.0D, eyeLocation));
        assertNotNull(HologramUtils.raytraceElement(
                identity, null, Display.Billboard.VERTICAL, anchor, 0.0D, 0.0D, 2.0D, 1.0D, eyeLocation));
    }

    private static Location eye(float yaw, float pitch) {
        return new Location(null, 0.0D, 64.0D, 0.0D, yaw, pitch);
    }

    private static float[] conjugate(float[] quaternion) {
        return new float[] { -quaternion[0], -quaternion[1], -quaternion[2], quaternion[3] };
    }

    private static void assertQuaternionDiffers(float[] expected, float[] actual) {
        for (int index = 0; index < expected.length; index++) {
            if (Math.abs(expected[index] - actual[index]) > DELTA) {
                return;
            }
        }
        throw new AssertionError("两个四元数不应相同");
    }

    private static void assertLocationEquals(Location expected, Location actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected.getX(), actual.getX(), DOUBLE_DELTA);
        org.junit.jupiter.api.Assertions.assertEquals(expected.getY(), actual.getY(), DOUBLE_DELTA);
        org.junit.jupiter.api.Assertions.assertEquals(expected.getZ(), actual.getZ(), DOUBLE_DELTA);
    }
}
