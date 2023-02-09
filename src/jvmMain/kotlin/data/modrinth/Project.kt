package data.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Project(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerialName("client_side") val clientSide: Side,
    @SerialName("server_side") val serverSide: Side,
    val body: String,
    @SerialName("additional_categories") val additionalCategories: List<String>? = null,
    @SerialName("issues_url") val issuesUrl: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("wiki_url") val wikiUrl: String? = null,
    @SerialName("discord_url") val discordUrl: String? = null,
    @SerialName("donation_urls") val donationUrls: JsonElement? = null,
    @SerialName("project_type") val projectType: ProjectType,
    val downloads: Int,
    @SerialName("icon_url") val iconUrl: String? = null,
    val team: String,
    @SerialName("moderator_message") val moderatorMessage: ModeratorMessage? = null,
    val published: String,
    val updated: String,
    val approved: String? = null,
    val followers: Int,
    val status: Status,
    val license: License,
    val versions: List<String>,
    val gallery: List<Gallery>,
)
