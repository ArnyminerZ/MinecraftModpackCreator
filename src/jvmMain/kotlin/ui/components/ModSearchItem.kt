package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.modrinth.ModrinthModCache
import data.modrinth.ProjectSearch
import data.packwiz.Mod
import data.packwiz.Project
import kotlinx.serialization.ExperimentalSerializationApi
import utils.async
import utils.doAsync

context(Project)
@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
fun ModSearchItem(project: ProjectSearch, onModInstalled: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        val cache = remember { ModrinthModCache(project.id) }
        var image: ImageBitmap? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            async {
                image = cache.image()
            }
        }

        Row(Modifier.fillMaxWidth()) {
            image?.let {
                Image(it, project.title, Modifier.size(90.dp).padding(8.dp).clip(RoundedCornerShape(12.dp)))
            } ?: Box(Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            val isModInstalled = remember {
                modsList.filterIsInstance<Mod>()
                    .filter { it.meta.update?.modrinth != null }
                    .map { it.meta.update!!.modrinth!! }
                    .find { modrinth -> modrinth.modId == project.id } != null
            }
            var installing by remember { mutableStateOf(false) }
            var removing by remember { mutableStateOf(false) }

            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        project.title,
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (isModInstalled)
                        IconButton(
                            onClick = doAsync {
                                removing = true
                                remove(project.id)
                                onModInstalled()
                                removing = false
                            },
                            enabled = !removing,
                        ) { Icon(Icons.Rounded.DeleteForever, "Remove") }
                }
                Text(
                    project.description,
                    Modifier.fillMaxWidth(),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(installing) { CircularProgressIndicator() }
                    TextButton(
                        onClick = doAsync {
                            installing = true
                            installModrinth(project.id)
                            onModInstalled()
                            installing = false
                        },
                        enabled = !isModInstalled && !installing,
                    ) {
                        Text(if (isModInstalled) "Installed" else "Install")
                    }
                }
            }
        }
    }
}
