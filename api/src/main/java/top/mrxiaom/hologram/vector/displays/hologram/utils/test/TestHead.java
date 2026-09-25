package top.mrxiaom.hologram.vector.displays.hologram.utils.test;

public class TestHead implements IAdventureTest {
    @Override
    public void test() throws Throwable {
        Class.forName("net.kyori.adventure.text.object.ObjectContents");
        Class.forName("net.kyori.adventure.text.object.PlayerHeadObjectContents");
    }
}
