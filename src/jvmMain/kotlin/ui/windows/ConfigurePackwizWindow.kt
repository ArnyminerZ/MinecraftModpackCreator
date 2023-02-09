package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import ui.screens.ConfigurePackwizScreen
import ui.theme.AppTheme

@ExperimentalMaterial3Api
@Composable
fun ConfigurePackwizWindow(onCloseRequest: (packwizPath: String?) -> Unit) {
    Window(onCloseRequest = { onCloseRequest(null) }, alwaysOnTop = true, title = "Configure Packwiz") {
        AppTheme {
            ConfigurePackwizScreen(window, onCloseRequest)
        }
    }
}
