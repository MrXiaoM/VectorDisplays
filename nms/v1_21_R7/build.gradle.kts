val setJavaVersion: (Int) -> Unit by ext
setJavaVersion(21)

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("com.mojang:datafixerupper:8.0.16")
    compileOnly("it.unimi.dsi:fastutil:8.5.15")
}
