package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuItemCompat(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    leadingImage: ImageBitmap? = null,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).then(modifier),
    ) {
        leadingIcon?.let { Icon(it, text, Modifier.padding(end = 8.dp)) }
        leadingImage?.let { Icon(it, text, Modifier.padding(end = 8.dp)) }
        Text(text)
    }
}
