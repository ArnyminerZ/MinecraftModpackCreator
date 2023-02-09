package data.modrinth

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.util.concurrent.locks.ReentrantLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import system.Cache
import system.Remote
import system.json

@ExperimentalSerializationApi
class ModrinthCache(private val modId: String) : Cache<Project>("modrinth", modId) {
    private val projectLock = ReentrantLock()

    private var project: Project? = null

    override fun fetch() {
        Remote.inputStream("https://api.modrinth.com/v2/project/$modId", mapOf("Accept" to "application/json"))
            .use { input -> json.decodeFromStream<Project>(input) }
            .also { project = it }
            .let {
                // Write the cache
                mainCacheFile.outputStream().use { output -> json.encodeToStream(project, output) }
            }
    }

    override fun read(): Project = mainCacheFile.inputStream().use { json.decodeFromStream(it) }

    override fun getOrFetch(): Project {
        project?.let { return it }

        projectLock.lock()

        if (!mainCacheFile.exists()) fetch()

        val project: Project = try {
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

    override fun image(): ImageBitmap? {
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