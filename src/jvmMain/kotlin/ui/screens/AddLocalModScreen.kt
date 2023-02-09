package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
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

context(Project, SnackbarHostState)
@Composable
fun AddLocalModScreen(onModAdded: () -> Unit) {
    val scope = rememberCoroutineScope()

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
                        showSnackbar(
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
