package system.storage

data class ConfigObserver <T: Any>(
    val key: ConfigKey<T>,
    val callback: (value: T?) -> Unit,
)
