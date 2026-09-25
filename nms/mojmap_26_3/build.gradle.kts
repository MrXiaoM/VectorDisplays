val setJavaVersion: (Int) -> Unit by ext
setJavaVersion(25)

repositories {
    maven("https://repo.mcio.dev/nms")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.3-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:26.3-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.3.11")
    compileOnly("com.mojang:datafixerupper:10.0.21")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
}
