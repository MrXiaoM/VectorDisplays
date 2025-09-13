import java.util.Locale

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
    javadoc {
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
                description.set("MrXiaoM's Bukkit plugin basic core")
                url.set("https://github.com/MrXiaoM/PluginBase")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/MrXiaoM/PluginBase/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("MrXiaoM")
                        email.set("mrxiaom@qq.com")
                    }
                }
                scm {
                    url.set("https://github.com/MrXiaoM/PluginBase")
                    connection.set("scm:git:https://github.com/MrXiaoM/PluginBase.git")
                    developerConnection.set("scm:git:https://github.com/MrXiaoM/PluginBase.git")
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
