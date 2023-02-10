package data.liteloader

import data.common.Version
import data.minecraft.MinecraftVersion
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

data class LiteLoaderVersion(
    val version: String,
    val repo: Repo,
    val snapshots: JsonElement? = null,
    val dev: Dev? = null,
): Version {
    @Serializable
    data class Proto(
        val repo: Repo,
        val snapshots: JsonElement? = null,
        val dev: Dev? = null,
    ) {
        fun version(version: String) = LiteLoaderVersion(version, repo, snapshots, dev)
    }

    override val loaderName: String = "liteloader"

    override val name: String = version

    override fun isCompatibleWith(version: MinecraftVersion): Boolean = version.id == this.version

    override fun toString(): String = name
}
