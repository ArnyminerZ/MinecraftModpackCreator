package data.modrinth

import java.io.InputStream
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import system.Remote
import system.json

@ExperimentalSerializationApi
object Modrinth {
    private val lock = ReentrantLock()

    suspend fun search(
        query: String,
        categories: List<Category> = emptyList(),
        projectType: ProjectType? = null,
        page: Int = 0,
        limit: Int = 10,
        index: SearchIndex = SearchIndex.relevance,
    ): SearchResult {
        if (lock.isLocked) throw IllegalStateException("Another search operation is being performed.")
        else try {
            lock.lock()
            val facets = arrayListOf<Pair<String, String>>()
                .apply {
                    categories.forEach { add(it.header to it.name) }
                    if (projectType != null) add("project_type" to projectType.name)
                }
                .takeIf { it.isNotEmpty() }
                ?.joinToString(",") { (k, v) -> "[\"$k:$v\"]" }

            val params = mutableMapOf<String, String>()
            if (query.isNotBlank()) params["query"] = query
            if (facets != null) params["facets"] = "[$facets]"
            if (page != 0) params["offset"] = (page * limit).toString()
            if (limit != 10) params["limit"] = limit.toString()
            if(index != SearchIndex.relevance) params["index"] = index.toString()

            return Remote.inputStream(
                "https://api.modrinth.com/v2/search?${
                    params.toList().joinToString("&") { (key, value) -> "$key=${URLEncoder.encode(value, Charsets.UTF_8)}" }
                }"
            ).use { input -> json.decodeFromStream(input) }
        } finally {
            lock.unlock()
        }
    }
}