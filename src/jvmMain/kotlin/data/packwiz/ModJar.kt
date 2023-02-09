package data.packwiz

import java.io.File

data class ModJar(
    val jarFile: File,
): ModModel {
    override val name: String = jarFile.name

    context(Project) override suspend fun remove(): Boolean = jarFile.delete()
}
