package data.packwiz

import data.modrinth.ModrinthModCache
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateModrinth(
    @SerialName("mod-id") val modId: String,
    val version: String,
) {
    fun cache() = ModrinthModCache(modId)
}
