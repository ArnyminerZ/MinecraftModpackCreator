package data.packwiz

import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val optional: Boolean,
    val default: Boolean? = null,
    val description: String? = null,
)
