package data.modrinth

import kotlinx.serialization.Serializable

@Serializable
data class Gallery(
    val url: String,
    val featured: Boolean,
    val title: String? = null,
    val description: String? = null,
    val created: String,
)
