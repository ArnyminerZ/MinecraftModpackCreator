package ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
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
fun ActionCard(
    text: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    actions: Array<Pair<String, () -> Unit>> = emptyArray(),
) {
    Card(modifier, colors = colors) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text,
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = if (actions.isEmpty()) 12.dp else 0.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            if (actions.isNotEmpty())
                Row(Modifier.fillMaxWidth().padding(start = 8.dp)) {
                    for ((btnText, onClick) in actions) {
                        TextButton(onClick) { Text(btnText) }
                    }
                }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun ErrorActionCard(
    text: String,
    modifier: Modifier = Modifier,
    actions: Array<Pair<String, () -> Unit>> = emptyArray(),
) {
    ActionCard(text, modifier, CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer), actions)
}

@Preview
@Composable
@ExperimentalMaterial3Api
fun ActionCardPreview() {
    ActionCard("This is an example text.", actions = arrayOf("Test" to {}))
}
