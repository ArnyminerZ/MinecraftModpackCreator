package data.liteloader

import data.common.Manifest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class LiteLoaderManifest(
    val meta: Meta,
    val versions: JsonElement,
): Manifest<LiteLoaderVersion> {
    /** Gets all the versions mapped into proper classes. */
    override fun getVersions(): List<LiteLoaderVersion> {
        val obj = versions.jsonObject
        return obj.keys.map { key ->
            val version = obj.getValue(key)
            val proto = json.decodeFromJsonElement<LiteLoaderVersion.Proto>(version)
            proto.version(key)
        }
    }
}
