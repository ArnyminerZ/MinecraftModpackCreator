package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Launches the given [block] of code in the IO context. Returns a job for supervising the request.
 */
fun async(block: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(Dispatchers.IO).launch(block = block)

/**
 * Launches the given [block] of code in the IO context. Returns a `() -> Unit` for passing directly into callbacks.
 */
fun doAsync(block: suspend CoroutineScope.() -> Unit): () -> Unit = {
    CoroutineScope(Dispatchers.IO).launch(block = block)
}
