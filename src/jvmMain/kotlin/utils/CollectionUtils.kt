package utils

fun <K, V, M: MutableMap<K, V>> M.appendIfNone(key: K, value: V): M {
    if (!containsKey(key))
        put(key, value)
    return this
}
