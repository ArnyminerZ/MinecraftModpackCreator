package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Dialog
import data.packwiz.Project
import kotlinx.serialization.ExperimentalSerializationApi
import ui.screens.NewProjectScreen
import ui.theme.AppTheme

@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun NewProjectWindow(onCloseRequest: () -> Unit, onProjectCreated: (project: Project) -> Unit) {
    Dialog(onCloseRequest = onCloseRequest, title = "Create new Project") {
        AppTheme {
            NewProjectScreen(onProjectCreated)
        }
    }
}
