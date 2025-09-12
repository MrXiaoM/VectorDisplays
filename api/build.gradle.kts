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
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://jitpack.io")
    maven("https://repo.helpch.at/releases/")
}
val shadowLink = configurations.create("shadowLink")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    testCompileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")

    compileOnly("net.kyori:adventure-platform-bukkit:4.4.0")
    testCompileOnly("net.kyori:adventure-text-serializer-plain:4.21.0")
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
