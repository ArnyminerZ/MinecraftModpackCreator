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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.packwiz.Project
import java.io.File
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ui.components.FilePicker
import utils.doAsync

context(Project)
@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun AddModScreen(onModAdded: () -> Unit) {
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
        with(snackbarHostState) {
            AnimatedVisibility(
                selectedTab == 0,
                modifier = Modifier.padding(paddingValues),
            ) {
                /* LOCAL */
                AddLocalModScreen(onModAdded)
            }
            AnimatedVisibility(
                selectedTab == 1,
                modifier = Modifier.padding(paddingValues),
            ) {
                /* MODRINTH */
                AddModrinthModScreen(onModAdded)
            }
        }
    }
}
