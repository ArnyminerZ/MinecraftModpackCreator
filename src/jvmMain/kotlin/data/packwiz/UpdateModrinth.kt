package data.packwiz

import data.modrinth.ModrinthCache
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateModrinth(
    @SerialName("mod-id") val modId: String,
    val version: String,
) {
    fun cache() = ModrinthCache(modId)
}
