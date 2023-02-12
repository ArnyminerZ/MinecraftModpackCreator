package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import data.packwiz.Project
import java.io.File
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import system.storage.Config
import system.storage.ConfigKey
import ui.dialog.AlertDialogCompat
import ui.toolbar.MainToolbar
import ui.windows.NewProjectWindow
import utils.with

@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@Composable
@Preview
@ExperimentalMaterial3Api
fun ApplicationScope.MainScreen(onProjectLoaded: (project: Project) -> Unit) {
    val config = remember { Config.get() }
    val snackbarHostState = SnackbarHostState()

    val currentProjectDir by config.state(ConfigKey.Project)
    var currentProject: Project? by remember { mutableStateOf(null) }
    var loadingProject by remember { mutableStateOf(false) }

    var showNewProjectWindow by remember { mutableStateOf(false) }
    if (showNewProjectWindow)
        NewProjectWindow(
            onCloseRequest = { showNewProjectWindow = false },
            onProjectCreated = {
                currentProject = it
                showNewProjectWindow = false
            },
        )

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

            config.delete(ConfigKey.Project)
            loadingProject = false

            val result = snackbarHostState.showSnackbar("Could not decode modpack toml file.", "View error")
            if (result == SnackbarResult.ActionPerformed) error = e.localizedMessage ?: e.message ?: e.toString()
        } catch (e: Exception) {
            e.printStackTrace()

            config.delete(ConfigKey.Project)
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
                if (projectDir == null)
                    currentProject = null
                else
                    loadProject(projectDir)
            }
    }

    LaunchedEffect(currentProject) {
        snapshotFlow { currentProject }
            .distinctUntilChanged()
            .collect {
                if (currentProjectDir != it?.baseDir?.path)
                    config[ConfigKey.Project] = it?.packToml?.path
                onProjectLoaded(it)
            }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                MainToolbar(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onCreateProject = { showNewProjectWindow = true },
                    onProjectPicked = { config[ConfigKey.Project] = it.path },
                    isCloseProjectAvailable = currentProject != null,
                    onCloseProjectRequested = { config.delete(ConfigKey.Project) },
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
            AnimatedVisibility(currentProject == null) {
                NoProjectLoadedScreen()
            }
        }
    }
}
