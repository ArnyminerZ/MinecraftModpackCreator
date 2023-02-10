package data.common

import data.minecraft.MinecraftVersion

interface Version {
    val loaderName: String

    val name: String

    fun isCompatibleWith(version: MinecraftVersion): Boolean
}
