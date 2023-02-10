package system

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.File
import utils.addTo
import utils.append

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
    private var observers = mapOf<String, List<(value: String?) -> Unit>>()

    /** Stores all the states listening for updates */
    @Volatile
    private var states = mapOf<String, List<MutableState<String?>>>()

    /** The file where all the configuration data is stored. */
    private val configFile = File(dataDir, "config.properties")

    private fun write(entries: Map<String, String>) {
        if (!configFile.parentFile.exists())
            configFile.parentFile.mkdirs()
        configFile
            .writer()
            .buffered()
            .use { writer ->
                for ((key, value) in entries)
                    writer.write("$key=$value\n")
            }
        observers.forEach { (k, l) -> l.forEach { it(entries[k]) } }
        states.forEach { (k, ls) -> ls.forEach { it.value = entries[k] } }
    }

    /** Reads all the data from the config file, and returns a map of key-value entries. */
    private fun getAll() = configFile
        // Check if the file exists
        .takeIf { it.exists() }
        // Read the file line by line
        ?.readLines()
        // Filter all blank and comment lines
        ?.filter { it.isNotBlank() || !it.startsWith("#") }
        // Map all lines to a pair of key-value
        ?.associate { it.indexOf('=').let { pos -> it.substring(0, pos) to it.substring(pos + 1) } }
        ?:
        // If the file doesn't exist, return empty list
        emptyMap()

    operator fun get(key: String): String? = getAll()[key]

    fun getValue(key: String): String = getAll().getValue(key)

    operator fun set(key: String, value: String?) {
        val all = getAll().toMutableMap()
        if (value == null)
            all.remove(key)
        else
            all[key] = value
        write(all)
    }

    fun delete(key: String) {
        println("CONFIG > Removing $key")
        set(key, null)
    }

    /**
     * Adds a new observer to the observers list.
     */
    fun observe(key: String, observer: (value: String?) -> Unit) {
        observers = observers.toMutableMap().addTo(key, observer)
    }

    fun state(key: String): State<String?> = mutableStateOf(get(key))
        .also { states = states.toMutableMap().addTo(key, it) }
}