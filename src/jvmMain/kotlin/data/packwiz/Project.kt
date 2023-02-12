package data.packwiz

import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import com.akuleshov7.ktoml.file.TomlFileReader
import com.akuleshov7.ktoml.file.TomlFileWriter
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.IOException
import kotlinx.serialization.serializer
import system.Packwiz
import system.storage.Config
import system.storage.ConfigKey

data class Project(val packToml: File, val baseDir: File, val pack: Pack, val modsList: List<ModModel>) {
    class Builder(packToml: File) {
        constructor(packTomlPath: String): this(File(packTomlPath))

        private val packToml: File

        init {
            if (packToml.name != "pack.toml")
                File(packToml.parentFile, "pack.toml").let {
                    if (!it.exists()) throw IllegalArgumentException("There's no pack.toml file available on the target directory ($it).")
                    this.packToml = packToml
                }
            else
                this.packToml = packToml
        }

        /**
         * Initializes a new project.
         * @throws FileNotFoundException If the given index file doesn't exist.
         * @throws TomlDecodingException If there's an error while decoding a toml file.
         * @throws IllegalArgumentException If the path of the [packToml] file contains a `|` character.
         */
        fun build(): Project {
            if (!packToml.exists()) throw FileNotFoundException("The given pack.toml file ($packToml) doesn't exist.")
            if (packToml.path.contains("|")) throw IllegalArgumentException("The path of the given pack.toml file ($packToml) contains an illegal character (|).")

            val baseDir = packToml.parentFile
            val pack = TomlFileReader.decodeFromFile<Pack>(serializer(), packToml.path)

            val modsDir = File(baseDir, "mods")
            val modsList = arrayListOf<ModModel>()
            println("Mods list:")
            modsDir.listFiles(FileFilter { it.extension == "toml" })?.forEach { toml ->
                println("  - $toml")
                modsList += Mod(toml, TomlFileReader.decodeFromFile(serializer(), toml.path))
            }
            println("Jar mods list:")
            modsDir.listFiles(FileFilter { it.extension == "jar" })?.forEach { jar ->
                println("  - $jar")
                modsList += ModJar(jar)
            }

            return Project(packToml, baseDir, pack, modsList)
                .also { Config.get().add(ConfigKey.RecentProjects, packToml.path) }
        }
    }

    private val modsDir = File(baseDir, "mods")

    val exists: Boolean
        get() = baseDir.exists()

    /**
     * Writes the contents of the current [pack] into the pack.toml file.
     */
    fun save(): Project {
        val packToml = File(baseDir, "pack.toml")
        TomlFileWriter(inputConfig = TomlInputConfig(), outputConfig = TomlOutputConfig())
            .encodeToFile(serializer(), pack, packToml.path)
        return this
    }

    /**
     * Removes the project from the filesystem.
     */
    fun delete() = baseDir.deleteRecursively()

    /**
     * Adds the given .jar [file] to the mods list, generating a `.pw.toml` file automatically.
     * @param file The `.jar` file to be added.
     * @throws NoSuchFileException If the source file doesn't exist.
     * @throws FileAlreadyExistsException If the destination file already exists.
     * @throws IOException If any error occurs while adding.
     */
    suspend fun add(file: File): Boolean {
        if (file.extension != "jar") throw IllegalArgumentException("The file must be a .jar file. File: $file")
        // First copy the .jar file into the mods folder
        file.copyTo(File(modsDir, file.name))
        // Run the refresh command so the toml file gets generated and the mod indexed
        return Packwiz.refresh(baseDir)
    }

    suspend fun remove(name: String) = Packwiz.remove(name, baseDir)

    suspend fun updateAll() = Packwiz.updateAll(baseDir)

    /**
     * Rebuilds the current project class, refreshing its contents in the process.
     */
    suspend fun rebuild(): Project = Packwiz.refresh(baseDir)
        .let { Builder(packToml).build() }

    suspend fun installModrinth(modId: String) = Packwiz.installModrinth(modId, baseDir)
}
