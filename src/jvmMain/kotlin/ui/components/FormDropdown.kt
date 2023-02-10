package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.common.StatusProvider

@Composable
fun <T> FormDropdown(
    value: String,
    onItemSelected: (item: T) -> Unit,
    items: Iterable<T>,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(modifier),
            label = label?.let { { Text(it) } },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = !expanded },
                    enabled = enabled,
                ) {
                    Icon(Icons.Rounded.ArrowDropDown, "Expand/Collapse")
                }
            }
        )
        DropdownMenuCompat(expanded, onDismissRequest = { expanded = !expanded }) {
            for (item in items) {
                DropdownMenuItemCompat(
                    item.toString(),
                    enabled = if (item is StatusProvider) item.enabled else true,
                ) { onItemSelected(item); expanded = false }
            }
        }
    }
}
