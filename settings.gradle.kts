rootProject.name = "VectorDisplays"

include(":api")
include(":nms")
File(rootDir, "nms").listFiles()?.forEach { file ->
    if (File(file, "build.gradle.kts").exists()) {
        include(":nms:${file.name}")
    }
}
include(":plugin")
