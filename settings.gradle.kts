rootProject.name = "VectorDisplays"

val onlyEnableNMS: List<String> = listOf(
    //"v1_20_R3"
)

include(":api")
include(":nms")
File(rootDir, "nms").listFiles()?.forEach { file ->
    if (File(file, "build.gradle.kts").exists()) {
        if (onlyEnableNMS.isEmpty() || onlyEnableNMS.contains(file.name)) {
            include(":nms:${file.name}")
        }
    }
}
include(":plugin")
