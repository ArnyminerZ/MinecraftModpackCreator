package data.liteloader

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val stream: String,
    val type: String,
    val url: String,
    val classifier: String,
)
