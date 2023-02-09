package ui.components

import androidx.compose.foundation.background
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenuItemCompat(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).then(modifier),
    ) {
        Text(text)
    }
}
