package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FormInput(
    value: String,
    onValueChange: ((String) -> Unit)?,
    label: String,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    action: Pair<ImageVector, () -> Unit>? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange ?: {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        label = { Text(label) },
        singleLine = singleLine,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = action != null || readOnly,
        isError = isError,
        trailingIcon = if (action != null) {
            { IconButton(action.second, enabled = enabled) { Icon(action.first, "") } }
        } else null,
    )
}

@Composable
fun FormInput(
    value: String,
    label: String,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    action: Pair<ImageVector, () -> Unit>? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) = FormInput(value, null, label, singleLine, maxLines, action, enabled = enabled, readOnly = true, isError)
