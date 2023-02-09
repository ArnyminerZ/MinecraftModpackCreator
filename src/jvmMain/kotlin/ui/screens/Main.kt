package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import data.packwiz.Project
import system.Config
import ui.toolbar.MainToolbar
import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import ui.dialog.AlertDialogCompat
import ui.windows.AddModWindow
import utils.with

@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@Composable
@Preview
@ExperimentalMaterial3Api
fun ApplicationScope.MainScreen() {
    val config = remember { Config.get() }
    val snackbarHostState = SnackbarHostState()

    val currentProjectDir by remember { config.state("current-project") }
    var currentProject: Project? by remember { mutableStateOf(null) }
    var loadingProject by remember { mutableStateOf(false) }

    var error: String? by remember { mutableStateOf(null) }

    if (error != null)
        AlertDialogCompat(
            onDismissRequest = { error = null },
            confirmButton = {
                TextButton(
                    onClick = { error = null },
                ) {
                    Text("Close")
                }
            },
            title = "Error",
            text = error ?: "",
        )

    suspend fun loadProject(path: File) {
        println("Loading project: $path")
        try {
            loadingProject = true
            currentProject = Project.Builder(path).build()
            loadingProject = false
        } catch (e: TomlDecodingException) {
            System.err.println("Could not decode pack.toml ($path).")
            e.printStackTrace()

            config.delete("current-project")
            loadingProject = false

            val result = snackbarHostState.showSnackbar("Could not decode modpack toml file.", "View error")
            if (result == SnackbarResult.ActionPerformed) error = e.localizedMessage ?: e.message ?: e.toString()
        } catch (e: Exception) {
            e.printStackTrace()

            config.delete("current-project")
            loadingProject = false

            snackbarHostState.showSnackbar("Could not load project.")
        }
    }

    LaunchedEffect(currentProjectDir) {
        currentProjectDir
            ?.let { File(it) }
            ?.let { loadProject(it) }

        snapshotFlow { currentProjectDir }
            .distinctUntilChanged()
            .map { it?.let { File(it) } }
            .collect { projectDir ->
                if (projectDir == null) return@collect
                loadProject(projectDir)
            }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                MainToolbar(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onProjectPicked = { config["current-project"] = it.path },
                    isCloseProjectAvailable = currentProject != null,
                    onCloseProjectRequested = { config.delete("current-project") },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { paddingValues ->
            AnimatedVisibility(loadingProject) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            AnimatedVisibility(currentProject != null) {
                currentProject?.with {
                    with(snackbarHostState) {
                        ProjectScreen(Modifier.padding(paddingValues)) { currentProject = it }
                    }
                }
            }
        }
    }
}
