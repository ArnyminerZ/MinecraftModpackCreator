package data.common

/**
 * Identifies an object that is able to give a list of versions that matches some criteria.
 */
interface Manifest <V: Version> {
    /** Returns a list of the versions available */
    fun getVersions(): List<V>
}
