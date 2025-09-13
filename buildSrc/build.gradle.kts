plugins {
    java
    `groovy-gradle-plugin`
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(gradleApi())
    api("com.google.code.gson:gson:2.10.1")

    api("org.apache.httpcomponents:httpclient:4.5.13")
    api("org.apache.httpcomponents:httpmime:4.5.13")
}

gradlePlugin {
    website.set("https://github.com/Karlatemp/maven-central-publish")
    vcsUrl.set("https://github.com/Karlatemp/maven-central-publish")

    plugins {
        register("maven-publishing") {
            id = "moe.karla.maven-publishing"
            implementationClass = "moe.karla.maven.publishing.MavenPublishingPlugin"

            displayName = "Maven Central Publishing"
            description = "Publishing your software to Maven Central"
        }
    }
}
