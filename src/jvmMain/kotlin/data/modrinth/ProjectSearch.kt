package data.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

@Serializable
data class ProjectSearch(
    @SerialName("project_id") val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerialName("client_side") val clientSide: Side,
    @SerialName("server_side") val serverSide: Side,
    @SerialName("display_categories") val displayCategories: List<String>,
    @SerialName("project_type") val projectType: ProjectType,
    val downloads: Int,
    val follows: Int,
    @SerialName("icon_url") val iconUrl: String? = null,
    val license: String,
    @SerialName("latest_version") val latestVersion: String,
    val versions: List<String>,
    val gallery: List<String>,
    @SerialName("date_created") val dateCreated: String,
    @SerialName("date_modified") val dateModified: String,
)
