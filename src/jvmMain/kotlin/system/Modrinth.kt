package system

import data.modrinth.Category
import data.modrinth.ProjectType
import data.modrinth.SearchIndex
import data.modrinth.SearchResult
import java.net.URLEncoder
import java.util.concurrent.locks.ReentrantLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

/**
 * Provides an interface for making requests to Modrinth.
 */
@ExperimentalSerializationApi
object Modrinth {
    private val lock = ReentrantLock()

    /**
     * Performs a search in Modrinth with the given criteria.
     * @param query The search query to perform.
     * @param categories Some categories to search for.
     * @param projectType The type of project to search for.
     * @param page The page to select. Offset gets calculated with this parameter and [limit].
     * @param limit The amount of elements to fetch for page.
     * @param index The order of search.
     * @return A [SearchResult] object with the results obtained from the server.
     * @throws IllegalStateException If there's another search being performed.
     */
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