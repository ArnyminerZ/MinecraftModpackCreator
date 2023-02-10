package system

import data.fabric.FabricManifest
import data.fabric.FabricVersion
import data.minecraft.MinecraftVersion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import utils.appendIfNone

@ExperimentalSerializationApi
object Fabric: ManifestProvider<FabricManifest>() {
    @Volatile
    var manifests: Map<MinecraftVersion, FabricManifest> = emptyMap()

    override suspend fun manifest(minecraftVersion: MinecraftVersion): FabricManifest =
        manifests[minecraftVersion] ?: run {
            if (lock.isLocked) {
                lock.lock()
                manifests.getValue(minecraftVersion).also { lock.unlock() }
            } else {
                lock.lock()
                println("FABRIC > Getting manifest for ${minecraftVersion.id}")
                manifests[minecraftVersion] ?: Remote.inputStream("https://meta.fabricmc.net/v2/versions/loader/${minecraftVersion.id}")
                    .use { it.bufferedReader().readText() }
                    .replace("(?!\")(true)(?!\")".toRegex(), "\"true\"")
                    .replace("(?!\")(false)(?!\")".toRegex(), "\"false\"")
                    .replace("\\[[ \n\r]*\\]".toRegex(), "[]")
                    .let { json.decodeFromString<List<FabricVersion>>(it) }
                    .let { FabricManifest(it) }
                    .also { manifests = manifests.toMutableMap().appendIfNone(minecraftVersion, it) }
                    .also { lock.unlock() }
            }
        }
}
