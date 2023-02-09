package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.packwiz.Project
import java.io.File
import kotlinx.coroutines.launch
import ui.components.FilePicker
import utils.doAsync

context(Project)
@ExperimentalMaterial3Api
@Composable
fun AddModScreen(onModAdded: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = SnackbarHostState()

    Scaffold(
        topBar = {
            TabRow(selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Local", Modifier.padding(vertical = 8.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Modrinth", Modifier.padding(vertical = 8.dp))
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        AnimatedVisibility(
            selectedTab == 0,
            modifier = Modifier.padding(paddingValues),
        ) {
            /* LOCAL */

            Column {
                var file by remember { mutableStateOf<File?>(null) }
                var adding by remember { mutableStateOf(false) }

                FilePicker(
                    file,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    filter = { _, f -> f.endsWith(".jar") },
                ) { file = it }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        enabled = file != null && !adding,
                        onClick = doAsync {
                            adding = true
                            val added = file?.let { add(it) }
                            adding = false

                            if(added == true) onModAdded()

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (added == true)
                                        "Mod added successfully."
                                    else
                                        "Could not add mod"
                                )
                            }
                        },
                    ) {
                        Text("Add Mod")
                    }
                }
            }
        }
        AnimatedVisibility(
            selectedTab == 1,
            modifier = Modifier.padding(paddingValues),
        ) {
            /* MODRINTH */
        }
    }
}
