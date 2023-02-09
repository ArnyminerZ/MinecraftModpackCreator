package ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

@Composable
fun FileDialog(
    parent: Frame? = null,
    title: String = "Choose a file",
    filter: FilenameFilter? = null,
    dir: File = File(System.getProperty("user.home")),
    onCloseRequest: (result: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, title, LOAD) {
            init {
                filter?.let { filenameFilter = it }
                directory = dir.toString()
            }

            override fun setVisible(b: Boolean) {
                super.setVisible(b)
                if (b) onCloseRequest(
                    if (file != null && directory != null)
                        File(File(directory), file)
                    else null
                )
            }
        }
    },
    dispose = FileDialog::dispose,
)
