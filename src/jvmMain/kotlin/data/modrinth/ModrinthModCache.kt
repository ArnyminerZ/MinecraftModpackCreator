package data.modrinth

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.util.concurrent.locks.ReentrantLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import system.Remote
import system.storage.Cache
import system.storage.json

@ExperimentalSerializationApi
class ModrinthModCache(private val modId: String) : Cache<ModrinthProject>("modrinth", modId) {
    private val projectLock = ReentrantLock()

    private var project: ModrinthProject? = null

    override suspend fun fetch() {
        Remote.inputStream("https://api.modrinth.com/v2/project/$modId", mapOf("Accept" to "application/json"))
            .use { input -> json.decodeFromStream<ModrinthProject>(input) }
            .also { project = it }
            .let {
                // Write the cache
                mainCacheFile.outputStream().use { output -> json.encodeToStream(project, output) }
            }
    }

    override suspend fun read(): ModrinthProject = mainCacheFile.inputStream().use { json.decodeFromStream(it) }

    override suspend fun getOrFetch(): ModrinthProject {
        project?.let { return it }

        projectLock.lock()

        if (!mainCacheFile.exists()) fetch()

        val project: ModrinthProject = try {
            read()
        } catch (e: SerializationException) {
            // Drop cache
            mainCacheFile.delete()
            // Fetch again
            fetch()
            // Return the fetched data
            read()
        }

        projectLock.unlock()
        return project
    }

    override suspend fun image(): ImageBitmap? {
        if (!imageCacheFile.exists())
            getOrFetch().iconUrl?.let { iconUrl ->
                Remote.inputStream(iconUrl)
                    .use {
                        // Write to cache file
                        imageCacheFile.outputStream().write(it.readAllBytes())
                    }
            }

        // Return the loaded image if exists, or null
        return if (imageCacheFile.exists())
            imageCacheFile.inputStream().use { loadImageBitmap(it) }
        else
            null
    }
}