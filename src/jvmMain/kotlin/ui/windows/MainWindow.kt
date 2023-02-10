package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.ExperimentalSerializationApi
import system.Packwiz
import system.storage.Config
import ui.screens.MainScreen
import ui.theme.AppTheme

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