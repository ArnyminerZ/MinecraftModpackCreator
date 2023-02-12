package system.storage

/** Defines a pair of a key with its value */
data class ConfigKeyValue <T: Any> (
    val key: ConfigKey<T>,
    val value: T?,
) {
    override fun toString(): String = "$key=${key.serialize(value)}"
}

operator fun <T: Any> Iterable<ConfigKeyValue<out T>>.get(key: ConfigKey<T>) = find { it.key == key }

fun <T: Any> Iterable<ConfigKeyValue<T>>.getValue(key: ConfigKey<T>) = find { it.key == key }!!
