import moe.karla.maven.publishing.MavenPublishingExtension.PublishingType

plugins {
    id("com.gradleup.shadow") version "8.3.0" apply false
    id("moe.karla.maven-publishing")
}

val sdkVersion = 17
group = "top.mrxiaom.hologram"
version = "1.0.1"

allprojects {
    group = rootProject.group

    if (File(projectDir, "src").isDirectory) {
        apply(plugin = "java")

        fun setJavaVersion(targetJavaVersion: Int) {
            extensions.configure<JavaPluginExtension> {
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
        setJavaVersion(sdkVersion)
        ext["setJavaVersion"] = ::setJavaVersion
    }
}

mavenPublishing {
    publishingType = PublishingType.AUTOMATIC
}

tasks {
    clean { delete(rootProject.file("out")) }
}
