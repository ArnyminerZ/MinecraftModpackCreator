package system

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import utils.appendIfNone

object Remote {
    private const val Agent = "ArnyminerZ/MinecraftModpackCreator/1.0.0-dev01 (arnyminerz.com)"

    fun inputStream(url: URL, headers: Map<String, String> = emptyMap()): InputStream {
        val conn = url.openConnection() as HttpURLConnection
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
            return try {
                inputStream(redirectUrl, headers)
            } catch (e: MalformedURLException) {
                inputStream(url.protocol + "://" + url.host + redirectUrl)
            }
        }
        if (responseCode in 200 until 300) {
            return conn.inputStream
        }
        throw RuntimeException("Could not make request to $url. Error ($responseCode): ${conn.responseMessage}")
    }

    fun inputStream(url: String, args: Map<String, String> = emptyMap()): InputStream =
        inputStream(URL(url), args)
}