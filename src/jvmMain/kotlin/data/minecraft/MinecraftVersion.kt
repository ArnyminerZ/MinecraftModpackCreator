package data.minecraft

import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersion(
    val id: String,
    val type: VersionType,
    val url: String,
    val time: String,
    val releaseTime: String,
    val sha1: String,
    val complianceLevel: Int,
) {
    override fun toString(): String = id
}
