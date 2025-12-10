subprojects {
    apply(plugin="java")
    repositories {
        if (java.util.Locale.getDefault().country == "CN") {
            maven("https://mirrors.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://libraries.minecraft.net/")
    }

    dependencies {
        add("compileOnly", "org.jetbrains:annotations:24.0.0")
        if (project.name.startsWith("v") || project.name.startsWith("mojmap")) {
            add("compileOnly", project(":nms:shared"))
        }
    }
}
