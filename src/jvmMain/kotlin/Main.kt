import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import kotlinx.serialization.ExperimentalSerializationApi
import ui.windows.MainWindow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalSerializationApi::class)
fun main() = application {
    MainWindow()
}
