package data.packwiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCurseForge(
    @SerialName("file-id") val fileId: Long,
    @SerialName("project-id") val projectId: Long,
)
