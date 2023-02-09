package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akuleshov7.ktoml.exceptions.TomlDecodingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.maven.artifact.versioning.ComparableVersion
import system.Packwiz
import system.launchUrl
import system.runCommand
import ui.components.ActionCard
import ui.components.ErrorActionCard
import ui.components.LoadingCard
import ui.dialog.FileDialog
import java.awt.Frame
import java.io.IOException
import java.net.URI

@ExperimentalMaterial3Api
@Preview
@Composable
fun ConfigurePackwizScreen(parent: Frame? = null, onPathFound: (path: String) -> Unit) {
    var packwizPath: String? by remember { mutableStateOf(null) }
    var isPackwizAvailable by remember { mutableStateOf(false) }
    var installingPackwiz: Boolean by remember { mutableStateOf(false) }
    var choosePackwiz: Boolean by remember { mutableStateOf(false) }
    var choosingPackwiz: Boolean by remember { mutableStateOf(false) }

    var isGoAvailable: Boolean? by remember { mutableStateOf(null) }
    var goVersion: ComparableVersion? by remember { mutableStateOf(null) }
    var isGoVersionFine: Boolean? by remember { mutableStateOf(null) }

    if (choosingPackwiz)
        FileDialog(parent) { choosingPackwiz = false; if (it != null) packwizPath = it.path }

    LaunchedEffect(isGoAvailable) {
        CoroutineScope(Dispatchers.IO).launch {
            isGoAvailable = try {
                val response = "go version".runCommand()
                val pieces = response.split(' ')
                goVersion = pieces[2]
                    // Remove the go prefix from version
                    .substring(2)
                    // Convert to SemVer
                    .let { ComparableVersion(it) }
                val requiredGoVersion = ComparableVersion("1.19")
                isGoVersionFine = goVersion!! >= requiredGoVersion
                true
            } catch (e: IOException) {
                false
            }
        }
    }
    LaunchedEffect(isGoAvailable) {
        snapshotFlow { isGoAvailable }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { available ->
                if (!available) return@collect
                packwizPath = try {
                    Packwiz.search()
                } catch (e: UnsupportedOperationException) {
                    null
                }
                isPackwizAvailable = packwizPath != null
            }
    }
    LaunchedEffect(packwizPath) {
        snapshotFlow { packwizPath }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { onPathFound(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val goDownloadAction: Pair<String, () -> Unit> = "Download" to {
            val launched = launchUrl(URI.create("https://golang.org/dl/"))
            if (!launched) println("Could not launch url")
        }

        AnimatedVisibility(isGoAvailable == null) {
            LoadingCard(
                "Checking if go is available...",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            )
        }
        AnimatedVisibility(isGoAvailable == false) {
            ErrorActionCard(
                "Go is not available.",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                actions = arrayOf(goDownloadAction),
            )
        }
        AnimatedVisibility(isGoVersionFine == false) {
            ErrorActionCard(
                "It's required to have a go version greater or equal than 1.19. You have $goVersion.",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                actions = arrayOf(goDownloadAction),
            )
        }

        AnimatedVisibility(isGoVersionFine == true && !isPackwizAvailable && !installingPackwiz) {
            ActionCard(
                "You don't have packwiz available in your system. Do you want us to download it for you?",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                actions = arrayOf(
                    "Install" to {
                        installingPackwiz = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val path = Packwiz.install()

                                packwizPath = path
                                installingPackwiz = false
                            } catch (e: UnsupportedOperationException) {
                                // The packwiz binary could not be found
                                choosePackwiz = true
                                installingPackwiz = false
                            }
                        }
                    },
                    "Choose" to { choosingPackwiz = true },
                ),
            )
        }
        AnimatedVisibility(installingPackwiz) {
            LoadingCard(
                "Installing packwiz...",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
            )
        }
        AnimatedVisibility(choosePackwiz) {
            ActionCard(
                "Packwiz was installed successfully, but we couldn't find the binary files. Please, select them manually",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                actions = arrayOf(
                    "Choose" to { choosingPackwiz = true }
                )
            )
        }
    }
}
