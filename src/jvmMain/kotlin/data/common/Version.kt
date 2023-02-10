package data.common

import data.minecraft.MinecraftVersion

/**
 * Provides some information about a version provided by a [Manifest].
 */
interface Version {
    /** The name of the loader used by packwiz. */
    val loaderName: String

    /** The name that identifies the version. */
    val name: String

    /**
     * Checks if this version is compatible with a given Minecraft version.
     * @return `true` if `this` is compatible with [version], `false` otherwise.
     */
    fun isCompatibleWith(version: MinecraftVersion): Boolean
}
