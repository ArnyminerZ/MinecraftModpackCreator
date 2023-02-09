package data.packwiz

import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import com.akuleshov7.ktoml.file.TomlFileReader
import com.akuleshov7.ktoml.file.TomlFileWriter
import kotlinx.serialization.serializer
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import system.Packwiz

data class Project(private val packToml: File, val baseDir: File, val pack: Pack, val modsList: List<Mod>) {
    class Builder(private val packToml: File) {
        /**
         * Initializes a new project.
         * @throws FileNotFoundException If the given index file doesn't exist.
         * @throws TomlDecodingException If there's an error while decoding a toml file.
         */
        fun build(): Project {
            if (!packToml.exists()) throw FileNotFoundException("The given pack.toml file ($packToml) doesn't exist.")

            val baseDir = packToml.parentFile
            val pack = TomlFileReader.decodeFromFile<Pack>(serializer(), packToml.path)

            val modsDir = File(baseDir, "mods")
            val modsList = arrayListOf<Mod>()
            println("Mods list:")
            modsDir.listFiles(FileFilter { it.extension == "toml" })?.forEach { toml ->
                println("  - $toml")
                modsList += Mod(toml, TomlFileReader.decodeFromFile(serializer(), toml.path))
            }

            return Project(packToml, baseDir, pack, modsList)
        }
    }

    /**
     * Writes the contents of the current [pack] into the pack.toml file.
     */
    fun save(): Project {
        val packToml = File(baseDir, "pack.toml")
        TomlFileWriter(inputConfig = TomlInputConfig(), outputConfig = TomlOutputConfig())
            .encodeToFile(serializer(), pack, packToml.path)
        return this
    }

    suspend fun updateAll() = Packwiz.updateAll(baseDir)

    /**
     * Rebuilds the current project class, refreshing its contents in the process.
     */
    fun rebuild(): Project = Builder(packToml).build()
}