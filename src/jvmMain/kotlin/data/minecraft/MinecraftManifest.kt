package data.minecraft

import kotlinx.serialization.Serializable

@Serializable
data class MinecraftManifest(
    val latest: LatestVersion,
    val versions: List<MinecraftVersion>,
)
