package data.fabric

import data.common.Version
import data.minecraft.MinecraftVersion
import kotlinx.serialization.Serializable

@Serializable
data class FabricVersion(
    val loader: FabricLoaderVersion,
    val intermediary: FabricIntermediary,
    val launcherMeta: FabricLauncherMeta,
): Version {
    override val loaderName: String = "fabric"

    override val name: String = loader.version

    override fun isCompatibleWith(version: MinecraftVersion): Boolean = intermediary.version == version.id

    override fun toString(): String = name
}
