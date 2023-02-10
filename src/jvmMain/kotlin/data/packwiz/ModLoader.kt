package data.packwiz

import data.common.StatusProvider
import java.util.Locale
import kotlinx.serialization.ExperimentalSerializationApi
import system.Fabric
import system.Liteloader
import system.ManifestProvider

@ExperimentalSerializationApi
enum class ModLoader(val provider: ManifestProvider<*>?): StatusProvider {
    fabric(Fabric), forge(null), liteloader(Liteloader), quilt(null);

    override val enabled: Boolean = provider != null

    override fun toString(): String =
        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}