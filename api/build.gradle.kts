import java.util.Locale

plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow")
}

version = rootProject.version

repositories {
    if (Locale.getDefault().country == "CN") {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
    }
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
}
val shadowLink = configurations.create("shadowLink")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")
    compileOnly("com.github.Tofaa2.EntityLib:spigot:2.4.11")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")

    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.23.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.23.0")
    compileOnly("org.jetbrains:annotations:24.0.0")

    for (item in project.project(":nms").subprojects) {
        if (item.name == "shared") {
            implementation(item)
        } else {
            add("shadowLink", item)
        }
    }
}
tasks {
    jar {
        archiveClassifier.set("api")
    }
    shadowJar {
        configurations.add(shadowLink)
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
            groupId = project.group.toString()
            artifactId = "VectorDisplays-API"
            version = project.version.toString()
        }
    }
}
