package data.minecraft

import kotlinx.serialization.Serializable

@Serializable
data class LatestVersion(
    val release: String,
    val snapshot: String,
)
