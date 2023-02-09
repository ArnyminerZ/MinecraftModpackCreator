package data.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val hits: List<ProjectSearch>,
    val offset: Int,
    val limit: Int,
    @SerialName("total_hits") val totalHits: Int,
)
