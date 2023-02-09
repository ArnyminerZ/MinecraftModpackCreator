package system

import java.io.File
import java.util.concurrent.TimeUnit
import java.lang.ProcessBuilder.Redirect
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun String.runCommand(
    workingDirectory: File = File(System.getProperty("user.home")),
): String = suspendCancellableCoroutine { cont ->
    val commandParts = split("\\s".toRegex()).toTypedArray()
    val proc = ProcessBuilder(*commandParts)
        .directory(workingDirectory)
        .redirectOutput(Redirect.PIPE)
        .redirectError(Redirect.PIPE)
        .start()
    cont.invokeOnCancellation { proc.destroyForcibly() }

    if (!proc.waitFor(5, TimeUnit.MINUTES)) {
        proc.destroy()
        cont.resumeWithException(RuntimeException("execution timed out: $this"))
    } else if (proc.exitValue() != 0) {
        cont.resumeWithException(RuntimeException("execution failed with code ${proc.exitValue()}: $this"))
    } else
        cont.resume(proc.inputStream.bufferedReader().readText())
}
