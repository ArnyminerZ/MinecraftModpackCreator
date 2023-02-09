package ui.icons

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Curseforge: ImageVector
    get() {
        if (_curseforge != null) {
            return _curseforge!!
        }
        _curseforge = ImageVector.Builder(
            name = "Curseforge",
            defaultWidth = 200.0.dp,
            defaultHeight = 200.0.dp,
            viewportWidth = 200.0F,
            viewportHeight = 200.0F,
        ).materialPath {
            verticalLineToRelative(0.0F)
            moveTo(6.307F, 5.581F)
            lineToRelative(0.391F, 1.675F)
            horizontalLineTo(0.0F)
            curveToRelative(1.06F, 1.228F, 2.902F, 1.73F, 4.409F, 2.009F)
            lineToRelative(1.228F, 3.293F)
            horizontalLineToRelative(0.67F)
            lineToRelative(0.391F, 1.061F)
            horizontalLineToRelative(-0.558F)
            lineToRelative(-0.949F, 3.07F)
            horizontalLineToRelative(9.321F)
            horizontalLineToRelative(-0.558F)
            horizontalLineToRelative(0.67F)
            curveTo(21.935F, 7.758F, 24.0F, 7.535F, 24.0F, 7.535F)
            verticalLineTo(5.581F)
            horizontalLineTo(6.307F)

            moveToRelative(9.377F, 8.428F)
            verticalLineToRelative(-0.167F)
            verticalLineToRelative(0.055F)
            horizontalLineToRelative(0.168F)

            close()
        }.build()
        return _curseforge!!
    }
private var _curseforge: ImageVector? = null

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun IconCurseforgePreview() {
    Image(imageVector = Icons.Curseforge, contentDescription = null)
}
