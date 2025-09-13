plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

val shadowLink = configurations.create("shadowLink")

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

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
    }
    build {
        dependsOn(create<Copy>("copyBuildArtifact") {
            dependsOn(shadowJar)
            from(shadowJar.get().outputs)
            rename { "${rootProject.name}-plugin-${rootProject.version}.jar" }
            into(project.file("out"))
        })
    }
    clean { delete(project.file("out")) }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}
