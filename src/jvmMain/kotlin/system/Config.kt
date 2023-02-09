package system

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.io.File

class Config private constructor(dataDir: File) {
    companion object {
        @Volatile
        private var INSTANCE: Config? = null

        fun get(): Config = INSTANCE ?: synchronized(Config) {
            INSTANCE?.let { return@synchronized it }

            Config(FileSystem.dataDir())
        }
    }

    private val configFile = File(dataDir, "config.properties")

    private val observers = mutableMapOf<String, List<(value: String?) -> Unit>>()

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

    fun delete(key: String) = set(key, null)

    fun observe(key: String, observer: (value: String?) -> Unit) {
        val list = observers[key]
        if (list == null)
            observers[key] = listOf(observer)
        else
            observers[key] = list.toMutableList().apply { add(observer) }
    }

    fun state(key: String): MutableState<String?> = mutableStateOf(get(key)).apply {
        observe(key) { this.value = it }
    }
}