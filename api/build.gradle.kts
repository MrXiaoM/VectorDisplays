import java.util.*

plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("com.gradleup.shadow")
    id("com.github.gmazzo.buildconfig")
}

version = rootProject.version

repositories {
    if (Locale.getDefault().country == "CN") {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        maven("https://lss233.littleservice.cn/repositories/minecraft/")
    }
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.pvphub.me/tofaa/")
    maven("https://jitpack.io")
}

java {
    withSourcesJar()
    withJavadocJar()
}

buildConfig {
    className("BuildConstants")
    packageName("top.mrxiaom.hologram.vector.displays")

    useJavaOutput()
    buildConfigField("String", "version", "\"${project.version}\"")
    buildConfigField("java.time.Instant", "BUILD_TIME", "java.time.Instant.ofEpochSecond(${System.currentTimeMillis() / 1000L}L)")
}

val shadowLink = configurations.create("shadowLink")
val shadowLinkWithLib = configurations.create("shadowLinkWithLib")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")
    compileOnly("io.github.tofaa2:spigot:3.1.0-SNAPSHOT")
    add("shadowLinkWithLib", "io.github.tofaa2:spigot:3.1.0-SNAPSHOT") {
        exclude("org.jetbrains", "annotations")
    }
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.2")

    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.23.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.23.0")
    compileOnly("org.jetbrains:annotations:24.0.0")

    for (item in project.project(":nms").subprojects) {
        if (item.name == "shared") {
            compileOnly(item)
            add("shadowLink", item)
        } else {
            add("shadowLink", item)
        }
    }
}
tasks {
    getByName<Jar>(sourceSets.main.get().sourcesJarTaskName) {
        from(project(":nms:shared").sourceSets.main.get().allSource)
    }
    shadowJar {
        configurations.add(shadowLink)
        configurations.add(project.configurations.runtimeClasspath.get())
    }
    this.register("shadowJarWithLib", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        from(sourceSets.main.map { it.output })
        configurations.add(shadowLink)
        configurations.add(shadowLinkWithLib)
        configurations.add(project.configurations.runtimeClasspath.get())
        archiveClassifier.set("with-lib")
        relocate("me.tofaa.entitylib", "${ext["shadowTarget"]}.entitylib")
    }
    build {
        dependsOn(shadowJar)
    }
    getByName<Javadoc>(sourceSets.main.get().javadocTaskName) {
        val task = project(":nms:shared").run {
            val taskName = this@run.sourceSets.main.get().javadocTaskName
            this@run.tasks.named<Javadoc>(taskName).get()
        }
        source += task.source
        (options as StandardJavadocDocletOptions).apply {
            locale("zh_CN")
            encoding("UTF-8")
            docEncoding("UTF-8")
            addBooleanOption("keywords", true)
            addBooleanOption("Xdoclint:none", true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "VectorDisplays-API"
            version = project.version.toString()

            artifact(tasks["shadowJar"]).classifier = null
            artifact(tasks["shadowJarWithLib"]).classifier = "for-plugin"
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set(artifactId)
                description.set("Terminal UI in air implementation for Minecraft.")
                url.set("https://github.com/MrXiaoM/VectorDisplays")
                licenses {
                    license {
                        name.set("AGPL-3.0")
                        url.set("https://github.com/MrXiaoM/VectorDisplays/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("MrXiaoM")
                        email.set("mrxiaom@qq.com")
                    }
                }
                scm {
                    url.set("https://github.com/MrXiaoM/VectorDisplays")
                    connection.set("scm:git:https://github.com/MrXiaoM/VectorDisplays.git")
                    developerConnection.set("scm:git:https://github.com/MrXiaoM/VectorDisplays.git")
                }
            }
        }
    }
}
signing {
    val signingKey = findProperty("signingKey")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications.getByName("maven"))
    }
}
