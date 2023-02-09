package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import data.packwiz.Project
import ui.screens.AddModScreen
import ui.theme.AppTheme

context(Project)
@ExperimentalMaterial3Api
@Composable
fun AddModWindow(onCloseRequest: () -> Unit, onModAdded: () -> Unit) {
    Window(onCloseRequest = { onCloseRequest() }, title = "Add new Mod") {
        AppTheme {
            AddModScreen(onModAdded)
        }
    }
}
