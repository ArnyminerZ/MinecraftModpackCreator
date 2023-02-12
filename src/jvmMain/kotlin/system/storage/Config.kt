package system.storage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.File
import system.FileSystem
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

            Config(FileSystem.dataDir()).also { INSTANCE = it }
        }
    }

    /** Stores all the observers registered */
    @Volatile
    private var observers = emptyList<ConfigObserver<Any>>()

    /** The file where all the configuration data is stored. */
    private val configFile = File(dataDir, "config.properties")

    private fun write(list: List<Pair<String, String>>) {
        // Convert the list of pairs into their corresponding ConfigKeyValues
        val entries = list.map { (key, value) ->
            val configKey = ConfigKey.valueOf(key)
            configKey.convertToPair(value)
        }
        // If the config file parent directory doesn't exist, create it
        if (!configFile.parentFile.exists()) configFile.parentFile.mkdirs()

        // Encode all the lines into a StringBuilder
        configFile.printWriter().use {  writer ->
            for ((key, value) in entries)
                writer.appendLine("$key=${key.serialize(value)}")
        }

        // For each observer
        for (observer in observers) {
            // Get its key and callbacks
            val (key, callback) = observer
            // Get the current value for the given entry
            val value = entries[key]?.value
            // Invoke the callback with the given value
            callback(value)
        }
    }

    /**
     * Returns a list of all the parameters stored as raw String pairs.
     */
    private fun getRaw(): List<Pair<String, String>> = configFile
        // Check if the file exists
        .takeIf { it.exists() }
        // Read the file line by line
        ?.readLines()
        // Filter all blank and comment lines
        ?.filter { it.isNotBlank() || !it.startsWith("#") }
        // Map all lines to a pair of key-value
        ?.associate { it.indexOf('=').let { pos -> it.substring(0, pos) to it.substring(pos + 1) } }
        ?.toList()
        ?: emptyList()

    /**
     * Gets the value stored at the given key.
     */
    operator fun <T : Any> get(key: ConfigKey<T>): T? = getRaw()
        .find { it.first == key.key }
        ?.let { (_, value) -> key.convert(value) }

    fun <T : Any> getValue(key: ConfigKey<T>): T = get(key)!!

    /**
     * Updates the value at the given [key] with the provided one.
     * @param key The key to store the value at.
     * @param value The value to store at [key], if null, the stored one gets removed.
     */
    operator fun <T : Any> set(key: ConfigKey<T>, value: T?) {
        val raw = getRaw().toMutableList()
        val index = raw.indexOfFirst { it.first == key.key }
        if (value == null) {
            if (index >= 0) raw.removeAt(index)
        } else {
            if (index >= 0) {
                // The item exists
                raw[index] = key.key to key.serialize(value)
            } else
                raw.add(key.key to key.serialize(value))
        }

        write(raw)
    }

    /**
     * Adds a value to a configuration key that contains a String Set.
     * @param key The key where the Set is stored at.
     * @param value The value to add.
     */
    fun add(key: ConfigKey<Set<String>>, value: String) {
        val list = get(key)?.toMutableSet() ?: mutableSetOf()
        list.add(value)

        set(key, list)
    }

    /**
     * Removes a value to a configuration key that contains a String Set.
     * @param key The key where the Set is stored at.
     * @param value The value to remove.
     */
    fun remove(key: ConfigKey<Set<String>>, value: String) {
        val list = get(key)?.toMutableSet() ?: mutableSetOf()
        list.remove(value)

        set(key, list.takeIf { it.isNotEmpty() })
    }

    fun <T : Any> delete(key: ConfigKey<T>) {
        println("CONFIG > Removing $key")
        set(key, null)
    }

    /**
     * Adds a new observer to the observers list.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> observe(key: ConfigKey<T>, observer: (value: T?) -> Unit) {
        observers = observers.toMutableList().append(ConfigObserver(key as ConfigKey<Any>) { observer(it as T?) })
    }

    fun <T : Any> state(key: ConfigKey<T>): State<T?> = mutableStateOf(get(key))
        .also { state -> observe(key) { state.value = it } }
}