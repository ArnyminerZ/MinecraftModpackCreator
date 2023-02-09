package ui.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import ui.dialog.FileDialog
import java.io.File
import ui.components.DropdownMenuCompat
import ui.components.DropdownMenuItemCompat

@Composable
@ExperimentalMaterial3Api
fun ApplicationScope.MainToolbar(
    modifier: Modifier = Modifier,
    onProjectPicked: (indexToml: File) -> Unit,
    isCloseProjectAvailable: Boolean,
    onCloseProjectRequested: () -> Unit,
) {
    var showProjectPicker by remember { mutableStateOf(false) }
    if (showProjectPicker)
        FileDialog(
            title = "Choose a project file",
            filter = { _, name -> name == "pack.toml" },
        ) { file ->
            if (file != null) onProjectPicked(file)
            showProjectPicker = false
        }

    Card(
        modifier = modifier,
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box {
                var expanded by remember { mutableStateOf(false) }
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 4.dp),
                ) {
                    Text("Project")
                }
                DropdownMenuCompat(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                ) {
                    DropdownMenuItemCompat(
                        "New Project",
                        onClick = {},
                    )
                    DropdownMenuItemCompat(
                        "Open Project",
                        onClick = { showProjectPicker = true; expanded = false },
                    )
                    if (isCloseProjectAvailable)
                        DropdownMenuItemCompat(
                            "Close Project",
                            onClick = onCloseProjectRequested,
                        )
                    Divider()
                    DropdownMenuItemCompat(
                        "Exit",
                        onClick = ::exitApplication,
                    )
                }
            }
        }
    }
}
