package ui.dialog

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogButton(text: String, onClick: () -> Unit): @Composable () -> Unit = {
    TextButton(onClick) {
        Text(text)
    }
}
