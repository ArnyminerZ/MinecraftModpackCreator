package data.fabric

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FabricLauncherMeta(
    val version: Int,
    val libraries: FabricLibraries,
    val mainClass: JsonElement,
)
