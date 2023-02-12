package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.packwiz.Project
import system.storage.Config
import system.storage.ConfigKey
import ui.dialog.AlertDialogCompat

/**
 * Shown when no project has been loaded, and shows some default actions as quick accesses.
 */
@Composable
@ExperimentalMaterial3Api
fun NoProjectLoadedScreen(
    onCreateProject: () -> Unit,
    onLoadProject: () -> Unit,
) {
    val config = remember { Config.get() }
    val recentProjects by config.state(ConfigKey.RecentProjects)

    var deletingProject: Project? by remember { mutableStateOf(null) }
    if (deletingProject != null)
        AlertDialogCompat(
            onDismissRequest = { deletingProject = null },
            title = "Deletion",
            text = "Are you sure that you want to delete the project \"${deletingProject?.pack?.name}\"? This will remove its directory from the file system and it will not be recoverable.",
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingProject?.let { project ->
                            project.delete()
                            config.remove(ConfigKey.RecentProjects, project.packToml.path)
                        }
                    },
                ) { Text("Delete") }
            },
            dismissButton = { TextButton({ deletingProject = null }) { Text("Cancel") } }
        )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            Modifier.widthIn(max = 700.dp).fillMaxWidth()
        ) {
            Text(
                "Welcome!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
            )
            ElevatedCard(
                Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    "Recent Projects",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                LazyColumn {
                    items(
                        recentProjects?.mapNotNull {
                            try {
                                Project.Builder(it).build()
                            } catch (e: IllegalArgumentException) {
                                System.err.println("There's a project in history that doesn't have a valid pack.toml: $it")
                                null
                            }
                        } ?: emptyList()
                    ) { project ->
                        ListItem(
                            headlineText = { Text(project.pack.name) },
                            supportingText = { Text(project.baseDir.path) },
                            overlineText = if (project.exists)
                                null
                            else {
                                { Text("Folder removed") }
                            },
                            colors = if (project.exists)
                                ListItemDefaults.colors()
                            else
                                ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    headlineColor = MaterialTheme.colorScheme.onErrorContainer,
                                    supportingColor = MaterialTheme.colorScheme.onErrorContainer,
                                ),
                            trailingContent = {
                                Row {
                                    IconButton(
                                        onClick = { config.remove(ConfigKey.RecentProjects, project.packToml.path) },
                                    ){ Icon(Icons.Rounded.Close, "Remove from recent projects") }
                                    IconButton(
                                        onClick = { deletingProject = project },
                                    ){ Icon(Icons.Rounded.DeleteForever, "Delete") }
                                    IconButton(
                                        onClick = { config[ConfigKey.Project] = project.packToml.path },
                                    ){ Icon(Icons.Rounded.ChevronRight, "Load") }
                                }
                            }
                        )
                    }
                    item {
                        if (recentProjects?.isNotEmpty() != true)
                            Text(
                                "No recent projects",
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp)
                            )
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onCreateProject,
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    Icon(Icons.Outlined.CreateNewFolder, "Create new Project")
                    Text("Create", Modifier.padding(start = 4.dp))
                }
                OutlinedButton(
                    onClick = onLoadProject,
                ) {
                    Icon(Icons.Outlined.Folder, "Load existingw Project")
                    Text("Load", Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
