package data.packwiz

import java.util.Locale

enum class ModLoader {
    fabric, forge, liteloader, quilt;

    override fun toString(): String =
        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}