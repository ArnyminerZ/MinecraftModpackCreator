package data.packwiz

import kotlinx.serialization.Serializable

@Serializable
data class Versions(
    val minecraft: String,
    val fabric: String? = null,
    val forge: String? = null,
    val liteloader: String? = null,
    val quilt: String? = null,
)
