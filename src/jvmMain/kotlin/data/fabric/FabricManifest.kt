package data.fabric

import data.common.Manifest

data class FabricManifest(
    private val versions: List<FabricVersion>,
): Manifest<FabricVersion> {
    override fun getVersions(): List<FabricVersion> = versions
}
