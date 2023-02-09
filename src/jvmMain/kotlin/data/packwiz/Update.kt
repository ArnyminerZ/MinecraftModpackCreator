package data.packwiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("modrinth") val modrinth: UpdateModrinth? = null,
    @SerialName("curseforge") val curseForge: UpdateCurseForge? = null,
)
