package utils

fun <K, V, M : MutableMap<K, V>> M.appendIfNone(key: K, value: V): M {
    if (!containsKey(key))
        put(key, value)
    return this
}

/** Adds the given [element] to the collection stored at [key], initializing it if necessary. */
@Suppress("UNCHECKED_CAST")
fun <K, V, C : Collection<V>, M : MutableMap<K, C>> M.addTo(key: K, element: V): M {
    set(
        key,
        // Get the list stored at key, or initialize an empty one
        getOrPut(key) { emptyList<V>() as C }
            // Make the list mutable
            .toMutableList()
            // Append the new element
            .append(element)
                // Cast the list to the correct type
                as C,
    )
    return this
}

/** Adds the given [element] to the list and returns `this`. */
fun <T, L : MutableCollection<T>> L.append(element: T): L {
    add(element)
    return this
}
