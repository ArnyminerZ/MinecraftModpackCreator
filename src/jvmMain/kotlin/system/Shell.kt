package system

import java.io.File
import java.util.concurrent.TimeUnit
import java.lang.ProcessBuilder.Redirect
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun Array<String>.runCommand(
    workingDirectory: File = File(System.getProperty("user.home"))
) = suspendCancellableCoroutine { cont ->
    val builder = ProcessBuilder(*this)
        .directory(workingDirectory)
        .redirectOutput(Redirect.PIPE)
        .redirectError(Redirect.PIPE)
    val proc = builder.start()
    cont.invokeOnCancellation { proc.destroyForcibly() }

    if (!proc.waitFor(5, TimeUnit.MINUTES)) {
        proc.destroy()
        cont.resumeWithException(RuntimeException("execution timed out: $this"))
    } else {
        val reader = proc.inputStream.reader().buffered()
        var line = reader.readLine()
        val responseBuilder = StringBuilder()
        while (line != null) {
            responseBuilder.appendLine(line)
            line = reader.readLine()
        }
        val text = responseBuilder.toString()
        if (proc.exitValue() != 0) {
            cont.resumeWithException(RuntimeException("execution failed with code ${proc.exitValue()}: ${builder.command().joinToString(" ")}\nResult: $text}"))
        } else
            cont.resume(text)
    }
}

suspend fun String.runCommand(
    workingDirectory: File = File(System.getProperty("user.home")),
): String = split("\\s".toRegex()).toTypedArray().runCommand(workingDirectory)
