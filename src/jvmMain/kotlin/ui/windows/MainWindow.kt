package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import system.Config
import system.Packwiz
import ui.dialog.FileDialog
import ui.screens.MainScreen
import ui.theme.AppTheme
import java.io.File
import java.io.FilenameFilter
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun ApplicationScope.MainWindow() {
    val config = remember { Config.get() }
    var showPackwizConfigure by remember { mutableStateOf(false) }
    val packwizPath by remember { config.state("packwiz") }

    var mainWindowTitle by remember { mutableStateOf("Modpack Creator") }

    suspend fun findPackwiz() {
        try {
            config["packwiz"] = Packwiz.search()
        } catch (e: UnsupportedOperationException) {
            println("Packwiz not supported. Showing configuration screen...")
            showPackwizConfigure = true
        }
    }

    LaunchedEffect(packwizPath) {
        if (packwizPath == null) findPackwiz()

        snapshotFlow { packwizPath }
            .distinctUntilChanged()
            .collect { path ->
                if (path != null) println("Packwiz: $path")
                else findPackwiz()
            }
    }

    Window(onCloseRequest = ::exitApplication, title = mainWindowTitle) {
        AppTheme {
            MainScreen { mainWindowTitle = "Modpack Creator - ${it.pack.name}" }
        }
    }

    if (showPackwizConfigure)
        ConfigurePackwizWindow { showPackwizConfigure = false }
}