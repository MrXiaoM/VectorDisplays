package top.mrxiaom.hologram.vector.displays.hologram.utils.test;

public class TestShadow implements IAdventureTest {
    @Override
    public void test() throws Throwable {
        Class.forName("net.kyori.adventure.util.ARGBLike");
        Class.forName("net.kyori.adventure.text.format.ShadowColor");
    }
}
