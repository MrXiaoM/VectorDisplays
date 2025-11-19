rootProject.name = "VectorDisplays"

val onlyEnableNMS: List<String> = listOf(
    //"v1_20_R3"
)

include(":api")
include(":nms")
File(rootDir, "nms").listFiles()?.forEach { file ->
    if (File(file, "build.gradle.kts").exists()) {
        val str = file.name
        if (str == "shared" || onlyEnableNMS.isEmpty() || onlyEnableNMS.contains(str)) {
            include(":nms:$str")
        }
    }
}
include(":vive-api")
include(":plugin")
