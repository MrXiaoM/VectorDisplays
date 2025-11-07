import java.util.*

plugins {
    id("java")
    id("maven-publish")
    id("signing")
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

java {
    withSourcesJar()
    withJavadocJar()
}

val shadowLink = configurations.create("shadowLink")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("it.unimi.dsi:fastutil:8.5.12")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")
    compileOnly("com.github.Tofaa2.EntityLib:spigot:df6fe0f084")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")

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
    jar {
        archiveClassifier.set("api")
    }
    getByName<Jar>(sourceSets.main.get().sourcesJarTaskName) {
        from(project(":nms:shared").sourceSets.main.get().allSource)
    }
    shadowJar {
        configurations.add(shadowLink)
        archiveClassifier.set("")
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
            links("https://hub.spigotmc.org/javadocs/spigot/")

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
            from(components.getByName("java"))
            groupId = project.group.toString()
            artifactId = "VectorDisplays-API"
            version = project.version.toString()

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
