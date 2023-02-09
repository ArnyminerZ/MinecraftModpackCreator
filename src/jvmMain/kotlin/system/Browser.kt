package system

import java.awt.Desktop
import java.io.IOException
import java.net.URI

/**
 * Launches the given [url] with the default's system browser.
 * @return `true` if the url was launched successfully, `false` is the system is not compatible with the action.
 * @throws IOException If there's an error while launching the URL.
 */
fun launchUrl(url: URI): Boolean {
    val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name") }
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    when {
        desktop != null && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(url)
        "mac" in osName -> Runtime.getRuntime().exec(arrayOf("open", url.toString()))
        "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec(arrayOf("xdg-open", url.toString()))
        else -> return false
    }
    return true
}
