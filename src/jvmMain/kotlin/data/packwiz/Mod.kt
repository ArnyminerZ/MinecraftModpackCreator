package data.packwiz

import java.io.File
import system.Packwiz
import utils.then

data class Mod(val tomlFile: File, val meta: ModMeta): ModModel {
    companion object {
        val Sample = Mod(File(""), ModMeta.Sample)
        val SampleModrinth = Sample.copy(meta = ModMeta.SampleModrinth)
    }

    override val name: String = meta.name

    /**
     * Tries to update the given mod to the latest available version.
     * @return `true` if the mod has been updated. `false` if there were no updates available.
     */
    context(Project)
    suspend fun update() = Packwiz.update(tomlFile.name.replace(".pw.toml", ""), this@Project.baseDir)

    /**
     * Tries to delete the given mod from the project.
     * @return `true` if the mod has been deleted. `false` if it could not have been deleted.
     */
    context(Project)
    override suspend fun remove() = Packwiz.remove(tomlFile.name.replace(".pw.toml", ""), this@Project.baseDir)
}
