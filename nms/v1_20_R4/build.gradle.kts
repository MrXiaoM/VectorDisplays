val setJavaVersion: (Int) -> Unit by ext
setJavaVersion(21)

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.6")
    compileOnly("com.mojang:brigadier:1.2.9")
    compileOnly("com.mojang:datafixerupper:7.0.14")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")
}
