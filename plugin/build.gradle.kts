import java.util.*

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    if (Locale.getDefault().country == "CN") {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        maven("https://lss233.littleservice.cn/repositories/minecraft/")
    }
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.helpch.at/releases/")
}

val shadowLink = configurations.create("shadowLink")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")
    compileOnly(project(":vive-api"))

    implementation("com.github.technicallycoded:FoliaLib:0.4.4")
    implementation("com.github.Tofaa2.EntityLib:spigot:b8ec880978")
    implementation(project(":api"))
    for (item in project.project(":nms").subprojects) {
        if (item.name == "shared") {
            implementation(item)
        } else {
            add("shadowLink", item)
        }
    }
}

tasks {
    shadowJar {
        configurations.add(shadowLink)
        val target = "top.mrxiaom.hologram.vector.displays"
        relocate("com.tcoded.folialib", "${target}.libs.folialib")
        relocate("me.tofaa.entitylib", "${target}.libs.entitylib")
    }
    val copyTask = create<Copy>("copyBuildArtifact") {
        dependsOn(shadowJar)
        from(shadowJar.get().outputs)
        rename { "${rootProject.name}-plugin-${rootProject.version}.jar" }
        into(rootProject.file("out"))
    }
    build { dependsOn(copyTask) }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to rootProject.version))
            include("plugin.yml")
        }
    }
}
