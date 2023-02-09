package data.modrinth

import kotlinx.serialization.Serializable

@Serializable
data class ModeratorMessage(
    val message: String,
    val body: String,
)
