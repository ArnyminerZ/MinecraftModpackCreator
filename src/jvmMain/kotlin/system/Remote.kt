package system

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import utils.appendIfNone

object Remote {
    private const val Agent = "ArnyminerZ/MinecraftModpackCreator/1.0.0-dev01 (arnyminerz.com)"

    suspend fun inputStream(url: URL, headers: Map<String, String> = emptyMap()): InputStream = suspendCancellableCoroutine { cont ->
        println("STREAM > $url")
        val conn = url.openConnection() as HttpURLConnection

        cont.invokeOnCancellation { conn.disconnect() }

        conn.connectTimeout = 15_000
        conn.readTimeout = 15_000
        headers
            .toMutableMap()
            .appendIfNone("User-Agent", Agent)
            .forEach { (key, value) -> conn.setRequestProperty(key, value) }
        conn.connect()

        val responseCode = conn.responseCode
        if (responseCode in 300 until 400) {
            val redirectUrl = conn.getHeaderField("Location")
            val stream = runBlocking {
                try {
                    inputStream(redirectUrl, headers)
                } catch (e: MalformedURLException) {
                    inputStream(url.protocol + "://" + url.host + redirectUrl)
                }
            }
            cont.resume(stream)
        }
        if (responseCode in 200 until 300)
            cont.resume(conn.inputStream)
        else
            cont.resumeWithException(RuntimeException("Could not make request to $url. Error ($responseCode): ${conn.responseMessage}"))
    }

    suspend fun inputStream(url: String, args: Map<String, String> = emptyMap()): InputStream = inputStream(URL(url), args)
}