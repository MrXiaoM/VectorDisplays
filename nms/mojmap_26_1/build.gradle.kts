val setJavaVersion: (Int) -> Unit by ext
setJavaVersion(25)

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:26.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("com.mojang:datafixerupper:9.0.19")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
}
