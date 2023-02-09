package utils

inline fun <T, R> T.with(block: T.() -> R) = with(this, block)

/**
 * Runs the given [block] of code only if `this` is `true`.
 */
inline fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}
