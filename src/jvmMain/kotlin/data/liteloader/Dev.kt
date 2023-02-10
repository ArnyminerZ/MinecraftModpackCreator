package data.liteloader

import kotlinx.serialization.Serializable

@Serializable
data class Dev(
    val fgVersion: String? = null,
    val mappings: String? = null,
    val mcp: String? = null,
)
