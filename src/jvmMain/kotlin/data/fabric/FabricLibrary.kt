package data.fabric

import kotlinx.serialization.Serializable

@Serializable
data class FabricLibrary(
    val name: String,
    val url: String? = null,
)
