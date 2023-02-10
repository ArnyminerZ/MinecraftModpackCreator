package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import data.common.Version
import data.minecraft.MinecraftManifest
import data.minecraft.MinecraftVersion
import data.minecraft.VersionType
import data.packwiz.ModLoader
import data.packwiz.Project
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import system.ManifestProvider
import system.Minecraft
import system.Packwiz
import ui.components.FormDropdown
import ui.components.FormInput
import ui.components.LoadingBox
import utils.doAsync

@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
fun NewProjectScreen(onProjectCreated: (project: Project) -> Unit) {
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        var name: String by remember { mutableStateOf("") }
        var description: String by remember { mutableStateOf("") }
        var author: String by remember { mutableStateOf(System.getProperty("user.name")) }
        var version: String by remember { mutableStateOf("1.0.0") }
        var path: String by remember { mutableStateOf("") }
        var minecraftVersion: MinecraftVersion? by remember { mutableStateOf(null) }
        var modLoader: ModLoader? by remember { mutableStateOf(null) }

        var loading by remember { mutableStateOf(false) }

        var versions: MinecraftManifest? by remember { mutableStateOf(null) }

        var modLoaderVersion: Version? by remember { mutableStateOf(null) }
        var modLoaderVersions: Collection<Version>? by remember { mutableStateOf(null) }
        var modLoaderVersionsHolder: Collection<Version>? by remember { mutableStateOf(null) }

        val valid = arrayOf(name, author, version, path).all { it.isNotBlank() } && modLoaderVersion != null && path.isNotBlank() && File(path).exists()

        var showFilePicker: Boolean by remember { mutableStateOf(false) }
        DirectoryPicker(
            show = showFilePicker,
            initialDirectory = System.getProperty("user.home"),
        ) { file ->
            showFilePicker = false
            file?.let { path = it }
        }

        fun Collection<Version>.filterVersions(): Collection<Version> =
            filter { minecraftVersion?.let { mcVer -> it.isCompatibleWith(mcVer) } ?: true }

        fun filterModLoaderVersions() {
            modLoaderVersions = modLoaderVersionsHolder?.filterVersions()
        }

        LaunchedEffect(path) {
            if (path.isNotBlank() && name.isEmpty()) name = File(path).nameWithoutExtension
        }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val manifest = Minecraft.manifest()
                scope.launch {
                    versions = manifest
                    minecraftVersion =
                        manifest.versions.find { it.id == manifest.latest.release } ?: manifest.versions[0]
                }
            }
        }

        LaunchedEffect(minecraftVersion) { filterModLoaderVersions() }
        LaunchedEffect(modLoaderVersionsHolder) { filterModLoaderVersions() }

        LaunchedEffect(modLoader) {
            if (minecraftVersion == null) return@LaunchedEffect

            loading = true
            val provider: ManifestProvider<*>? = modLoader?.provider
            val manifest = withContext(Dispatchers.IO) { provider?.manifest(minecraftVersion!!) }

            modLoaderVersionsHolder = manifest?.getVersions()
            modLoaderVersion = modLoaderVersionsHolder?.filterVersions()?.firstOrNull()

            loading = false
        }

        if (versions == null)
            LoadingBox()
        else Column(Modifier.fillMaxSize().padding(paddingValues)) {
            Column(Modifier.fillMaxWidth().weight(1f).padding(8.dp).verticalScroll(rememberScrollState())) {
                Text(
                    "Create new Project",
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                )

                FormInput(
                    value = path,
                    label = "Project location *",
                    action = Icons.Rounded.CreateNewFolder to { showFilePicker = true },
                    enabled = !loading,
                    isError = path.isBlank() || !File(path).exists(),
                )
                if (File(path).list()?.isNotEmpty() == true)
                    Text("The directory is not empty. This may cause issues.")

                FormInput(
                    value = name,
                    onValueChange = { name = it },
                    isError = name.isBlank(),
                    label = "Project Name *",
                    enabled = !loading,
                )

                FormInput(
                    value = description,
                    onValueChange = { description = it },
                    label = "Project Description",
                    enabled = !loading,
                    singleLine = false,
                    maxLines = 5,
                )

                FormInput(
                    value = author,
                    onValueChange = { author = it },
                    isError = author.isBlank(),
                    label = "Author *",
                    enabled = !loading,
                )

                FormInput(
                    value = version,
                    onValueChange = { version = it },
                    isError = version.isBlank(),
                    label = "Project Version *",
                    enabled = !loading,
                )

                var showReleases: Boolean by remember { mutableStateOf(true) }
                var showSnapshots: Boolean by remember { mutableStateOf(false) }
                var showBeta: Boolean by remember { mutableStateOf(false) }
                var showAlpha: Boolean by remember { mutableStateOf(false) }
                FormDropdown(
                    value = minecraftVersion?.id ?: "",
                    onItemSelected = { minecraftVersion = it },
                    label = "Minecraft Version *",
                    items = versions?.versions?.filter {
                        when (it.type) {
                            VersionType.release -> showReleases
                            VersionType.snapshot -> showSnapshots
                            VersionType.old_beta -> showBeta
                            VersionType.old_alpha -> showAlpha
                        }
                    } ?: emptyList(),
                    enabled = !loading,
                )
                Row(Modifier.fillMaxWidth()) {
                    FilterChip(
                        showReleases,
                        onClick = { showReleases = !showReleases },
                        label = { Text("Releases") },
                        Modifier.padding(horizontal = 4.dp),
                        enabled = !loading,
                    )
                    FilterChip(
                        showSnapshots,
                        onClick = { showSnapshots = !showSnapshots },
                        label = { Text("Snapshots") },
                        Modifier.padding(horizontal = 4.dp),
                        enabled = !loading,
                    )
                    FilterChip(
                        showBeta,
                        onClick = { showBeta = !showBeta },
                        label = { Text("Beta") },
                        Modifier.padding(horizontal = 4.dp),
                        enabled = !loading,
                    )
                    FilterChip(
                        showAlpha,
                        onClick = { showAlpha = !showAlpha },
                        label = { Text("Alpha") },
                        Modifier.padding(horizontal = 4.dp),
                        enabled = !loading,
                    )
                }

                FormDropdown(
                    value = modLoader?.toString() ?: "None",
                    onItemSelected = { modLoader = it },
                    label = "Mod Loader *",
                    items = ModLoader.values().asIterable(),
                    enabled = !loading,
                )
                FormDropdown(
                    value = modLoaderVersion?.toString() ?: "No version found",
                    onItemSelected = { modLoaderVersion = it },
                    label = "Mod Loader Version *",
                    enabled = !loading && modLoaderVersions != null && modLoaderVersion != null,
                    items = modLoaderVersions
                        ?.filter { minecraftVersion?.let { mcVer -> it.isCompatibleWith(mcVer) } ?: true }
                        ?: emptyList(),
                )
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = doAsync {
                        Packwiz.createProject(
                            File(path),
                            name,
                            description,
                            author,
                            version,
                            minecraftVersion!!,
                            modLoaderVersion!!,
                        ).let(onProjectCreated)
                    },
                    enabled = valid,
                ) { Text("Save") }
            }
            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        }
    }
}
