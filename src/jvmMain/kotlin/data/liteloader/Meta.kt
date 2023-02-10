package data.liteloader

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val description: String,
    val authors: String,
    val url: String,
    val updated: String,
    val updatedTime: Long,
)
