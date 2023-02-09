package data.packwiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Download(
    @SerialName("hash-format") val hashFormat: String,
    val hash: String,
    val url: String? = null,
    val mode: String? = null,
)
