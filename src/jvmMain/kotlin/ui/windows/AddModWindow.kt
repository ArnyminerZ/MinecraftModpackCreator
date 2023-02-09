package ui.windows

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import data.packwiz.Project
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ui.screens.AddModScreen
import ui.theme.AppTheme
import utils.doAsync
import utils.with

@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun AddModWindow(project: Project, onCloseRequest: () -> Unit, onUpdateProject: (newProject: Project) -> Unit) {
    Window(onCloseRequest = { onCloseRequest() }, title = "Add new Mod") {
        val scope = rememberCoroutineScope()
        AppTheme {
            var projectState by remember { mutableStateOf(project) }

            with(projectState) {
                AddModScreen(
                    doAsync {
                        rebuild().let {
                            println("Rebuilt project!")
                            scope.launch {
                                projectState = it
                                onUpdateProject(it)
                            }
                        }
                    }
                )
            }
        }
    }
}
