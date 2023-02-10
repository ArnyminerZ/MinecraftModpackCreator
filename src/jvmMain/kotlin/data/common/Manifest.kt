package data.common

interface Manifest <V: Version> {
    fun getVersions(): List<V>
}
