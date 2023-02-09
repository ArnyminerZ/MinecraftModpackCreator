package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.data.SidebarItem

@ExperimentalMaterial3Api
@Composable
fun Sidebar(selectedIndex: Int, items: List<SidebarItem>, onItemSelected: (index: Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, end = 4.dp)
            .padding(vertical = 8.dp)
            .widthIn(max = 300.dp),
    ) {
        for ((index, item) in items.withIndex()) {
            val (icon, text) = item
            OutlinedButton(
                onClick = { onItemSelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedIndex == index) MaterialTheme.colorScheme.secondaryContainer else Color.Unspecified,
                    contentColor = if (selectedIndex == index) MaterialTheme.colorScheme.onSecondaryContainer else Color.Unspecified,
                ),
                border = if (selectedIndex == index) BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.onSecondaryContainer) else null,
            ) {
                Icon(icon, text)
                Text(text, Modifier.weight(1f).padding(start = 8.dp))
            }
        }
    }
}
