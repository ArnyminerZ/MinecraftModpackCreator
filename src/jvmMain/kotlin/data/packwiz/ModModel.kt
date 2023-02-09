package data.packwiz

interface ModModel {
    val name: String

    context(Project)
    suspend fun remove(): Boolean
}
