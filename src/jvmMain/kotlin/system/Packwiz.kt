package system

import com.lordcodes.turtle.shellRun
import data.common.Version
import data.minecraft.MinecraftVersion
import data.packwiz.Project
import data.packwiz.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import utils.quoted

object Packwiz {
    /**
     * Tries to install packwiz, and returns its path after installing.
     * @throws UnsupportedOperationException If packwiz was downloaded correctly, but the binary could not be found.
     */
    suspend fun install(): String {
        // Install packwiz
        "go install github.com/packwiz/packwiz@latest".runCommand()

        // Once installed, check if it's reachable
        return search()
    }

    /**
     * Searches for the packwiz file.
     * @return The path of the packwiz file.
     * @throws UnsupportedOperationException If packwiz could not be found.
     */
    suspend fun search(): String {
        // First check in path
        try {
            "packwiz --help".runCommand()
            return "packwiz"
        } catch (e: IOException) {
            println("Packwiz is not available in path.")
        }

        // Search in GOBIN
        val goBin: File? = System.getenv("GOBIN")?.let { File(it) }
        if (goBin != null)
            try {
                val packwiz = File(goBin, "packwiz").path
                "$packwiz --help".runCommand()
                return packwiz
            } catch (e: IOException) {
                println("Packwiz is not available in GOBIN ($goBin).")
            }
        else
            println("GOBIN is not defined.")

        // Search in home
        val userHome = File(System.getProperty("user.home"))
        val goBinaries = File(userHome, "go/bin")
        try {
            val packwiz = File(goBinaries, "packwiz").path
            "$packwiz --help".runCommand()
            return packwiz
        } catch (e: IOException) {
            println("Packwiz is not available in home go binaries ($goBinaries).")
        }

        throw UnsupportedOperationException("The packwiz command is not available.")
    }

    /**
     * Tries to update the given mod.
     * @return `true` if the mod has been updated. `false` if there were no updates available.
     */
    suspend fun update(name: String, projectDir: File): Boolean {
        println("Trying to update mod $name...")
        val packwiz = Config.get()["packwiz"]
        val result = "$packwiz update $name".runCommand(projectDir)
        println("Update response: $result")
        if (result.contains("already up to date"))
            return false
        return true
    }

    suspend fun updateAll(projectDir: File): List<UpdateResult> {
        println("Trying to update all mods...")
        val packwiz = Config.get()["packwiz"]
        val resultLines = "$packwiz update -a -y".runCommand(projectDir).split('\n')
        return resultLines
            .filter { it.contains("->") }
            .map { line ->
                val namePos = line.indexOf(':')
                val sepPos = line.indexOf("->")
                UpdateResult(
                    line.substring(0, namePos),
                    line.substring(namePos + 1, sepPos).trim(),
                    line.substring(sepPos + 2).trim(),
                )
            }
    }

    /**
     * Removes the given mod from the project.
     * @return `true` if the mod was removed correctly. `false` if something went wrong.
     */
    suspend fun remove(name: String, projectDir: File): Boolean {
        println("Removing mod $name...")
        val packwiz = Config.get()["packwiz"]
        val result = "$packwiz remove $name".runCommand(projectDir)
        return result.contains("removed successfully")
    }

    /**
     * Runs the refresh command on the project's directory.
     */
    suspend fun refresh(projectDir: File): Boolean {
        println("Refreshing $projectDir...")
        val packwiz = Config.get()["packwiz"]
        val result = "$packwiz refresh".runCommand(projectDir)
        println("  Refresh: $result")
        return result.contains("Index refreshed")
    }

    suspend fun installModrinth(modId: String, projectDir: File) {
        println("Installing $modId from Modrinth in $projectDir...")
        val packwiz = Config.get()["packwiz"]
        val result = "$packwiz mr install $modId".runCommand(projectDir)
        println("Result: $result")
    }

    suspend fun createProject(
        directory: File,
        name: String,
        description: String,
        author: String,
        version: String,
        minecraftVersion: MinecraftVersion,
        modLoaderVersion: Version,
    ): Project {
        println("Creating new project in $directory for $minecraftVersion...")
        val packwiz = Config.get().getValue("packwiz")
        arrayOf(
            packwiz, "init",
            "--name", name,
            "--version", version,
            "--author", author,
            "--mc-version", minecraftVersion.id,
            "--modloader", modLoaderVersion.loaderName,
            "--${modLoaderVersion.loaderName}-version", modLoaderVersion.name,
        ).runCommand(directory)

        // TODO: Set description

        return Project.Builder(File(directory, "pack.toml"))
            .build()
            .let { project ->
                if (description.isNotBlank())
                    project
                        .copy(pack = project.pack.copy(description = description))
                        .save()
                        .rebuild()
                else
                    project
            }
    }
}