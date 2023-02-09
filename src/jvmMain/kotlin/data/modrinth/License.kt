package data.modrinth

import kotlinx.serialization.Serializable

@Serializable
data class License(
    val id: String,
    val name: String,
    val url: String? = null,
)
