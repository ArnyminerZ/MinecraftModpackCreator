package system.storage

import kotlin.reflect.full.isSubclassOf

sealed class ConfigKey <out T: Any> (
    val key: String,
    val type: Type<T>,
) {
    object Packwiz: ConfigKey<String>("packwiz", Type.STRING)
    object Project: ConfigKey<String>("project", Type.STRING)
    object RecentProjects: ConfigKey<List<String>>("recent-projects", Type.STRING_LIST)

    sealed class Type <out C: Any> {
        object STRING: Type<String>() {
            override fun convert(source: String): String = source
        }
        object INTEGER: Type<Int>() {
            override fun convert(source: String): Int = source.toInt()
        }
        object BOOLEAN: Type<Boolean>() {
            override fun convert(source: String): Boolean = source.toBoolean()
        }
        object DOUBLE: Type<Double>() {
            override fun convert(source: String): Double = source.toDouble()
        }
        object STRING_LIST: Type<List<String>>() {
            override fun convert(source: String): List<String> = source.split("|")
        }

        abstract fun convert(source: String): C
    }

    companion object {
        @JvmStatic
        private val map by lazy {
            ConfigKey::class.sealedSubclasses
                .filter { kClass -> kClass.isSubclassOf(ConfigKey::class) }
                .map { kClass -> kClass.objectInstance }
                .filterIsInstance<ConfigKey<*>>()
                .also { list -> println("Config keys: ${list.joinToString { it.key }}") }
                .associateBy { it.key }
        }

        @JvmStatic
        fun valueOf(key: String) = map.also { println("MAP: $it") }.get(key) ?: throw IllegalArgumentException(
            "Could not find a ConfigKey with key $key"
        )

        @JvmStatic
        fun values() = map.values.toTypedArray()
    }
}
