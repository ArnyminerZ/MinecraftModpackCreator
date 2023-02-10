package system

import data.liteloader.LiteLoaderManifest
import data.liteloader.LiteLoaderVersion
import data.minecraft.MinecraftVersion
import java.io.InputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@ExperimentalSerializationApi
object Liteloader : ManifestProvider<LiteLoaderManifest>() {
    @Volatile
    var manifest: LiteLoaderManifest? = null

    override suspend fun manifest(minecraftVersion: MinecraftVersion): LiteLoaderManifest = manifest ?: run {
        if (lock.isLocked) {
            lock.lock()
            manifest!!.also { lock.unlock() }
        } else {
            lock.lock()
            manifest ?: Remote.inputStream("https://dl.liteloader.com/versions/versions.json")
                .use<InputStream, LiteLoaderManifest> { Json.decodeFromStream(it) }
                .also { manifest = it }
                .also { lock.unlock() }
        }
    }
}