package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.packwiz.Project
import ui.components.Sidebar
import ui.data.SidebarItem

context(SnackbarHostState, Project)
@ExperimentalMaterial3Api
@Composable
fun ProjectScreen(modifier: Modifier = Modifier, onUpdateProject: (project: Project) -> Unit) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth().weight(1f)) {
            var selectedMenu by remember { mutableStateOf(0) }
            Sidebar(
                selectedMenu,
                listOf(
                    SidebarItem(Icons.Rounded.Dashboard, "General"),
                    SidebarItem(Icons.Rounded.List, "Mods (${modsList.size})"),
                ),
            ) { selectedMenu = it }

            when(selectedMenu) {
                0 -> ProjectGeneralPanel(onUpdateProject)
                1 -> ProjectModsPanel()
            }
        }
        Text(
            baseDir.path,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
