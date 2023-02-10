package system.storage

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.File
import system.FileSystem
import utils.addTo

/**
 * A Singleton for managing the project's configuration. Stores all the configuration in the user's data dir
 * ([FileSystem.dataDir]).
 *
 * The configuration is stored in a file called `config.properties` with a key-value pairs format separated by `=`.
 *
 * Example:
 * ```properties
 * key1=example
 * key2=new example
 * ```
 */
class Config private constructor(dataDir: File) {
    companion object {
        @Volatile
        private var INSTANCE: Config? = null

        fun get(): Config = INSTANCE ?: synchronized(Config) {
            INSTANCE?.let { return@synchronized it }

            Config(FileSystem.dataDir())
        }
    }

    /** Stores all the observers registered */
    @Volatile
    private var observers = mapOf<ConfigKey<*>, List<(value: Any?) -> Unit>>()

    /** Stores all the states listening for updates */
    @Volatile
    private var states = mapOf<ConfigKey<*>, List<MutableState<Any?>>>()

    /** The file where all the configuration data is stored. */
    private val configFile = File(dataDir, "config.properties")

    private fun write(entries: Map<ConfigKey<Any>, Any?>) {
        if (!configFile.parentFile.exists())
            configFile.parentFile.mkdirs()
        configFile
            .writer()
            .buffered()
            .use { writer ->
                for ((key, value) in entries)
                    writer.write("${key.key}=$value\n")
            }
        observers.forEach { (k, l) -> l.forEach { it(entries[k]) } }
        states.forEach { (k, ls) -> ls.forEach { it.value = entries[k] } }
    }

    /** Reads all the data from the config file, and returns a map of key-value entries. */
    private fun getAll(): Map<ConfigKey<*>, Any?> = configFile
        // Check if the file exists
        .takeIf { it.exists() }
        // Read the file line by line
        ?.readLines()
        // Filter all blank and comment lines
        ?.filter { it.isNotBlank() || !it.startsWith("#") }
        // Map all lines to a pair of key-value
        ?.associate { it.indexOf('=').let { pos -> it.substring(0, pos) to it.substring(pos + 1) } }
        ?.mapKeys { (key, _) -> ConfigKey.valueOf(key) }
        ?.mapValues { (key, value) -> key.type.convert(value) }
        ?:
        // If the file doesn't exist, return empty list
        emptyMap()

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(key: ConfigKey<T>): T? = getAll()[key] as? T

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> getValue(key: ConfigKey<T>): T = getAll().getValue(key) as T

    operator fun <T: Any> set(key: ConfigKey<T>, value: T?) {
        val all = getAll().toMutableMap()
        if (value == null)
            all.remove(key)
        else
            all[key] = value
        write(all)
    }

    fun <T: Any> delete(key: ConfigKey<T>) {
        println("CONFIG > Removing $key")
        set(key, null)
    }

    /**
     * Adds a new observer to the observers list.
     */
    fun <T: Any> observe(key: ConfigKey<T>, observer: (value: T?) -> Unit) {
        observers = observers.toMutableMap().addTo(key, observer)
    }

    fun <T: Any> state(key: ConfigKey<T>): State<T?> = mutableStateOf(get<T>(key))
        .also { states = states.toMutableMap().addTo(key, it) }
}