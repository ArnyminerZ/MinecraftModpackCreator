package ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun AlertDialogCompat(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    title: String,
    text: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(title) },
        text = { Text(text) },
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
