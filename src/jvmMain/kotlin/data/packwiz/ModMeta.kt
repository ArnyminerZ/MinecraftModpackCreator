package data.packwiz

import kotlinx.serialization.Serializable

@Serializable
data class ModMeta(
    val name: String,
    val filename: String,
    val download: Download,
    val side: ModSide? = null,
    val update: Update? = null,
    val option: Option? = null,
) {
    companion object {
        val Sample = ModMeta("Example mod", "filename.jar", download = Download("", ""))
        val SampleModrinth = Sample.copy(update = Update(UpdateModrinth("1234", "1234")))
    }

    override fun toString(): String = name
}
