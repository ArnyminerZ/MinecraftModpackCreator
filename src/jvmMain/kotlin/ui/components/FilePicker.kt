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

@Composable
fun FilePicker(
    file: File?,
    modifier: Modifier = Modifier,
    label: String = "Pick a file",
    fileExtension: String? = null,
    onFileSelected: (file: File) -> Unit,
) {
    var showingPicker by remember { mutableStateOf(false) }
    com.darkrockstudios.libraries.mpfilepicker.FilePicker(
        show = showingPicker,
        fileExtension = fileExtension,
        initialDirectory = System.getProperty("user.home")
    ) { path ->
        path?.let { File(it) }?.let(onFileSelected)
        showingPicker = false
    }

    // TODO: Move to FormInput
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
