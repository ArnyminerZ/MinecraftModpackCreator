package system

import androidx.compose.ui.graphics.ImageBitmap
import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

@ExperimentalSerializationApi
abstract class Cache <T> (namespace: String, private val name: String) {
    private val cacheDir = File(FileSystem.dataDir(), "cache")
    private val namespaceCacheDir = File(cacheDir, namespace)

    protected val mainCacheFile = File(namespaceCacheDir, name)
    protected val imageCacheFile = File(namespaceCacheDir, "$name-image")

    init {
        if (!namespaceCacheDir.exists()) namespaceCacheDir.mkdirs()
    }

    internal abstract suspend fun fetch()

    internal abstract suspend fun read(): T

    abstract suspend fun getOrFetch(): T

    abstract suspend fun image(): ImageBitmap?
}