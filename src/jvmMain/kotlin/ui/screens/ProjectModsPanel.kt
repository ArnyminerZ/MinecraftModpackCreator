package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.packwiz.Mod
import data.packwiz.ModJar
import data.packwiz.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ui.components.ModItem
import ui.windows.AddModWindow
import utils.async
import utils.doAsync

context(SnackbarHostState, Project)
@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
fun RowScope.ProjectModsPanel(onUpdateProject: (newProject: Project) -> Unit) {
    val scope = rememberCoroutineScope()

    var showNewModWindow by remember { mutableStateOf(false) }
    if (showNewModWindow) AddModWindow(
        { showNewModWindow = false },
        onModAdded = doAsync { onUpdateProject(rebuild()) }
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .padding(start = 4.dp, end = 8.dp)
            .padding(vertical = 8.dp)
            .fillMaxHeight(),
    ) {
        var search by remember { mutableStateOf("") }
        var updating by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextField(
                    search,
                    { search = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search...") },
                    trailingIcon = {
                        if (search.isNotBlank())
                            IconButton(
                                onClick = { search = "" },
                            ) {
                                Icon(Icons.Rounded.Close, "Clear")
                            }
                    }
                )
                TextButton(
                    enabled = !updating,
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            updating = true
                            scope.launch {
                                showSnackbar(
                                    "Searching for updates...",
                                    duration = SnackbarDuration.Indefinite
                                )
                            }
                            val updateResult = updateAll()
                            if (updateResult.isEmpty())
                                scope.launch { showSnackbar("No update found.") }
                            else
                                scope.launch { showSnackbar("${updateResult.size} mods updated.") }
                            updating = false
                        }
                    }
                ) {
                    Text("Update All")
                }
                TextButton(
                    onClick = { showNewModWindow = true },
                ) {
                    Text("Add")
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val filtered = modsList.filter {
                    when (it) {
                        is Mod -> it.meta.name.contains(search, true)
                        is ModJar -> it.jarFile.name.contains(search, true)
                        else -> true
                    }
                }
                items(filtered) { mod ->
                    ModItem(
                        mod, !updating,
                        onUpdateRequested = object : suspend () -> Unit {
                            override suspend fun invoke() {
                                mod as Mod
                                val updated = mod.update()
                                if (updated)
                                    scope.launch { showSnackbar("Mod Updated successfully!") }
                                else
                                    scope.launch { showSnackbar("No updates available!") }
                            }
                        }.takeIf { mod is Mod },
                        onRemoveRequested = {
                            async {
                                val deleted = mod.remove()
                                if (deleted)
                                    scope.launch { showSnackbar("Mod deleted successfully!") }
                                else
                                    scope.launch { showSnackbar("Deletion error!") }
                                onUpdateProject(rebuild())
                            }
                        },
                    )
                }
                item {
                    AnimatedVisibility(filtered.isEmpty()) {
                        Text(
                            "No mods found.",
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}
