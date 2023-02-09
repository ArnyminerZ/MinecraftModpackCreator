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
class ModrinthCategoriesCache : Cache<List<Category>>("modrinth", "meta-categories") {
    private val lock = ReentrantLock()

    private var categories: List<Category>? = null

    override suspend fun fetch() {
        Remote.inputStream("https://api.modrinth.com/v2/tag/category", mapOf("Accept" to "application/json"))
            .use { input -> json.decodeFromStream<List<Category>>(input) }
            .also { categories = it }
            .let {
                // Write the cache
                mainCacheFile.outputStream().use { output -> json.encodeToStream(categories, output) }
            }
    }

    override suspend fun read(): List<Category> = mainCacheFile.inputStream().use { json.decodeFromStream(it) }

    override suspend fun getOrFetch(): List<Category> {
        categories?.let { return it }

        lock.lock()

        if (!mainCacheFile.exists()) fetch()

        val value = try {
            read()
        } catch (e: SerializationException) {
            // Drop cache
            mainCacheFile.delete()
            // Fetch again
            fetch()
            // Return the fetched data
            read()
        }

        lock.unlock()
        return value
    }

    override suspend fun image(): ImageBitmap = throw UnsupportedOperationException()
}