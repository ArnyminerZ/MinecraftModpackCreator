package system

import java.io.File

object FileSystem {
    fun dataDir(): File {
        val appData = System.getenv("APPDATA")?.let { File(it) }
        return if (appData != null) {
            File(appData, "MinecraftModpackCreator")
        } else {
            val home = File(System.getProperty("user.home"))
            File(home, ".MinecraftModpackCreator")
        }
    }
}