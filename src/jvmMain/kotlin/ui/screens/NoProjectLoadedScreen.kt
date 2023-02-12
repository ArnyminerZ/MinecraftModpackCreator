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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.packwiz.Project
import system.storage.Config
import system.storage.ConfigKey

/**
 * Shown when no project has been loaded, and shows some default actions as quick accesses.
 */
@Composable
@ExperimentalMaterial3Api
fun NoProjectLoadedScreen(
    onCreateProject: () -> Unit,
    onLoadProject: () -> Unit,
    onSelectProject: (project: Project) -> Unit,
) {
    val recentProjects by Config.get().state(ConfigKey.RecentProjects)

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
                                        onClick = { /* TODO: Remove from recent projects */ },
                                    ){ Icon(Icons.Rounded.Close, "Remove from recent projects") }
                                    IconButton(
                                        onClick = { /* TODO: Delete */ },
                                    ){ Icon(Icons.Rounded.DeleteForever, "Delete") }
                                    IconButton(
                                        onClick = { onSelectProject(project) },
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
