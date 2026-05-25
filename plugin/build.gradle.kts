import java.util.*

plugins {
    id("java")
    id("com.gradleup.shadow")
}

repositories {
    if (Locale.getDefault().country == "CN") {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        maven("https://crystal.app.lss233.com/repositories/minecraft/")
    }
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.pvphub.me/tofaa/")
    maven("https://repo.helpch.at/releases/")
    maven("https://jitpack.io")

val shadowLink: Configuration = configurations.create("shadowLink")

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
    compileOnly(project(":vive-api"))

    implementation(project(":api"))
    implementation("com.github.technicallycoded:FoliaLib:0.4.4") {
        exclude("org.jetbrains", "annotations")
    }
    implementation("io.github.tofaa2:spigot:3.3.2-SNAPSHOT") {
        exclude("org.jetbrains", "annotations")
    }
    implementation("net.kyori:adventure-text-minimessage:4.26.1") { isTransitive = false }
    for (item in project.project(":nms").subprojects) {
        if (item.name == "shared") {
            implementation(item)
        }
        add("shadowLink", item)
    }
}

tasks {
    shadowJar {
        configurations.add(shadowLink)
        configurations.add(project.configurations.runtimeClasspath.get())
        mapOf(
            "com.tcoded.folialib" to "folialib",
            "me.tofaa.entitylib" to "entitylib",
            "net.kyori.adventure.text.minimessage" to "text.minimessage",
        ).forEach { (original, target) ->
            relocate(original, "${ext["shadowTarget"]}.$target")
        }
    }
    val copyTask = register<Copy>("copyBuildArtifact") {
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
