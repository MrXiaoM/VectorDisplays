import moe.karla.maven.publishing.MavenPublishingExtension.PublishingType

plugins {
    id("com.gradleup.shadow") version "9.3.0" apply false
    id("com.github.gmazzo.buildconfig") version "6.0.7" apply false
    id("moe.karla.maven-publishing")
}

group = ext["project.group"].toString()
version = ext["project.version"].toString()

allprojects {
    group = rootProject.group

    if (File(projectDir, "src").isDirectory) {
        apply(plugin = "java")

        fun setJavaVersion(targetJavaVersion: Int) {
            extensions.configure<JavaPluginExtension> {
                disableAutoTargetJvm()
                val javaVersion = JavaVersion.toVersion(targetJavaVersion)
                if (JavaVersion.current() < javaVersion) {
                    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
                }
            }
            tasks.withType<JavaCompile>().configureEach {
                options.encoding = "UTF-8"
                if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
                    options.release.set(targetJavaVersion)
                }
            }
        }
        setJavaVersion(rootProject.ext["sdkVersion"].toString().toInt())
        ext["setJavaVersion"] = ::setJavaVersion
        ext["shadowTarget"] = "top.mrxiaom.hologram.vector.displays.libs"
    }
}

mavenPublishing {
    publishingType = PublishingType.AUTOMATIC
}

tasks {
    clean { delete(rootProject.file("out")) }
}
