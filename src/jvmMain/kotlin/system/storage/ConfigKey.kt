package system.storage

import kotlin.reflect.full.isSubclassOf

sealed class ConfigKey<T : Any>(
    val key: String,
    private val type: Type<T>,
) {
    object Packwiz : ConfigKey<String>("packwiz", Type.STRING)
    object Project : ConfigKey<String>("project", Type.STRING)
    object RecentProjects : ConfigKey<Set<String>>("recent-projects", Type.STRING_SET)

    sealed interface Type<C : Any> {
        object STRING : Type<String> {
            override fun convert(source: String): String = source

            override fun serialize(value: String?): String = value ?: ""
        }

        object INTEGER : Type<Int> {
            override fun convert(source: String): Int = source.toInt()

            override fun serialize(value: Int?): String = value?.toString() ?: ""
        }

        object BOOLEAN : Type<Boolean> {
            override fun convert(source: String): Boolean = source.toBoolean()

            override fun serialize(value: Boolean?): String = value?.toString() ?: ""
        }

        object DOUBLE : Type<Double> {
            override fun convert(source: String): Double = source.toDouble()

            override fun serialize(value: Double?): String = value?.toString() ?: ""
        }

        object STRING_SET : Type<Set<String>> {
            override fun convert(source: String): Set<String> = source.split("|").toSet()

            override fun serialize(value: Set<String>?): String = value?.joinToString("|") ?: ""
        }

        /** Parses the given source string into the correct type for this key. */
        fun convert(source: String): C

        /** Serializes an object of the type of the key into a string */
        fun serialize(value: C?): String
    }

    companion object {
        @JvmStatic
        private val map by lazy {
            ConfigKey::class.sealedSubclasses
                .filter { kClass -> kClass.isSubclassOf(ConfigKey::class) }
                .map { kClass -> kClass.objectInstance }
                .filterIsInstance<ConfigKey<Any>>()
                .associateBy { it.key }
        }

        @JvmStatic
        fun valueOf(key: String) = map[key] ?: throw IllegalArgumentException(
            "Could not find a ConfigKey with key $key"
        )

        @JvmStatic
        fun values() = map.values.toTypedArray()
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (other !is ConfigKey<*>) return false
        return key == other.key
    }

    override fun hashCode(): Int = key.hashCode()

    fun convertToPair(value: String) = ConfigKeyValue(this, convert(value))

    fun convert(source: String) = type.convert(source)

    fun serialize(value: T?) = type.serialize(value)
}
