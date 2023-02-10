package data.fabric

import kotlinx.serialization.Serializable

@Serializable
data class FabricLoaderVersion(
    val separator: String,
    val build: Int,
    val maven: String,
    val version: String,
    val stable: Boolean
)
