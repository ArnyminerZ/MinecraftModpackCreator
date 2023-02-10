package data.fabric

import kotlinx.serialization.Serializable

@Serializable
data class FabricLibraries(
    val client: List<FabricLibrary>,
    val common: List<FabricLibrary>,
    val server: List<FabricLibrary>,
)
