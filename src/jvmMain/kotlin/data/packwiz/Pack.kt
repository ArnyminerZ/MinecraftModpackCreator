package data.packwiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pack(
    val name: String,
    val author: String? = null,
    val version: String? = null,
    val description: String? = null,
    @SerialName("pack-format") val format: String,
    val index: Index,
    val versions: Versions,
)
