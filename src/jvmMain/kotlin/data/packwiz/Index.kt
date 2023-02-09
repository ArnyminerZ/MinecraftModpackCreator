package data.packwiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Index(
    val file: String,
    @SerialName("hash-format") val hashFormat: String,
    val hash: String,
)
