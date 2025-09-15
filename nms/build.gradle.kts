subprojects {
    apply(plugin="java")
    repositories {
        if (java.util.Locale.getDefault().country == "CN") {
            maven("https://mirrors.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.rosewooddev.io/repository/public/")
        maven("https://libraries.minecraft.net/")
    }

    dependencies {
        add("compileOnly", "org.jetbrains:annotations:24.0.0")
        if (project.name.startsWith("v")) {
            add("compileOnly", project(":nms:shared"))
        }
    }
}
