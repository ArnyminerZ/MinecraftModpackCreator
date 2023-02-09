package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.io.File
import java.io.FilenameFilter
import ui.dialog.FileDialog

@Composable
fun FilePicker(
    file: File?,
    modifier: Modifier = Modifier,
    label: String = "Pick a file",
    filter: FilenameFilter? = null,
    onFileSelected: (file: File) -> Unit,
) {
    var showingPicker by remember { mutableStateOf(false) }

    if (showingPicker)
        FileDialog(filter = filter) {
            it?.let(onFileSelected)
            showingPicker = false
        }

    OutlinedTextField(
        file?.path ?: "",
        {},
        Modifier.clickable {
            showingPicker = true
        }.then(modifier),
        label = { Text(label) },
        enabled = false,
        readOnly = true,
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        ),
    )
}
