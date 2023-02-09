package ui.icons

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Modrinth: ImageVector
    get() {
        if (_modrinth != null) {
            return _modrinth!!
        }
        _modrinth = ImageVector.Builder(
            name = "Modrinth",
            defaultWidth = 200.0.dp,
            defaultHeight = 200.0.dp,
            viewportWidth = 200.0F,
            viewportHeight = 200.0F,
        ).materialPath {
            verticalLineToRelative(0.0F)
            arcToRelative(12.3F, 12.298F, 0.0F, false, false, 8.85F, 5.639F)
            lineToRelative(1.0F, 0.97F)

            arcToRelative(8.51F, 8.499F, 0.0F, false, true, 2.99F, 2.0F)
            arcToRelative(8.38F, 8.379F, 0.0F, false, true, 2.16F, 3.449F)
            arcToRelative(6.9F, 6.9F, 0.0F, false, true, 0.4F, 2.8F)

            close()
        }.build()
        return _modrinth!!
    }
private var _modrinth: ImageVector? = null

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun IconModrinthPreview() {
    Image(imageVector = Icons.Modrinth, contentDescription = null)
}