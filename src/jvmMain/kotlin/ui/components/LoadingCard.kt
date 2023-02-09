package ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterial3Api
fun LoadingCard(
    text: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
) {
    Card(modifier, colors = colors) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(Modifier.padding(8.dp))
            Text(text, Modifier.padding(start = 8.dp))
        }
    }
}
