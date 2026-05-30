rootProject.name = "VectorDisplays"

val onlyEnableNMS: List<String> = listOf(
    //"v1_21_R7",
    //"mojmap_1_21_11",
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
include(":plugin")
