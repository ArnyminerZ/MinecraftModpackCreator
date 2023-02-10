package system

import data.minecraft.MinecraftManifest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@ExperimentalSerializationApi
object Minecraft {
    /** Fetches the current Minecraft version manifest from Mojang's servers. */
    suspend fun manifest(): MinecraftManifest =
        Remote.inputStream("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
            .use { Json.decodeFromStream(it) }
}