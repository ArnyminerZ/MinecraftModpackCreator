package ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import data.packwiz.ModMeta
import data.packwiz.ModSide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import ui.dialog.AlertDialogCompat
import ui.dialog.DialogButton
import utils.async
import utils.doAsync

@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
fun ModItem(modMeta: ModMeta, updateAvailable: Boolean, onUpdateRequested: suspend () -> Unit, onRemoveRequested: suspend () -> Unit) {
    val localInspectionMode = LocalInspectionMode.current

    var showDeletionDialog by remember { mutableStateOf(false) }
    if (showDeletionDialog)
        AlertDialogCompat(
            onDismissRequest = { showDeletionDialog = false },
            confirmButton = DialogButton("Delete", doAsync { onRemoveRequested() }),
            dismissButton = DialogButton("Cancel") { showDeletionDialog = false },
            title = "Deletion",
            text = "Are you sure that you want to remove ${modMeta.name}? This cannot be undone.",
        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        var bitmap: ImageBitmap? by remember { mutableStateOf(null) }

        val modrinth = modMeta.update?.modrinth
        val curseForge = modMeta.update?.curseForge

        // Load bitmap only if not in preview mode
        if (!localInspectionMode)
            LaunchedEffect(modMeta) {
                if (modrinth != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val cache = modrinth.cache()

                        try {
                            cache.getOrFetch()
                            bitmap = cache.image()
                        } catch (e: SerializationException) {
                            System.err.println("Could not serialize mod \"${modrinth.modId}\". Error: ${e.localizedMessage}")
                        }
                    }
                }
            }

        Row(
            modifier = Modifier.fillMaxWidth(1f),
        ) {
            if (localInspectionMode)
                Box(Modifier.size(90.dp).background(Color.Black))
            else
                bitmap?.let { Image(it, modMeta.name, Modifier.size(90.dp)) } ?: Spacer(Modifier.size(90.dp))

            Column(
                Modifier.weight(1f)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            ) {
                Row {
                    Text(
                        modMeta.name,
                        Modifier
                            .weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        Icons.Rounded.Refresh,
                        "Refresh",
                        Modifier.size(22.dp)
                            .padding(end = 4.dp)
                            .clickable {  },
                    )
                    if (modrinth != null)
                        Image(
                            painterResource("modrinth.svg"),
                            "Modrinth",
                            modifier = Modifier.size(22.dp),
                        )
                    if (curseForge != null)
                        Image(
                            painterResource("curseforge.svg"),
                            "Curseforge",
                            modifier = Modifier.size(22.dp),
                        )
                }
                if (modMeta.side == ModSide.client)
                    Chip("Client")
                else if (modMeta.side == ModSide.server)
                    Chip("Server")

                Spacer(Modifier.weight(1f))

                Row {
                    var updateButtonEnabled by remember { mutableStateOf(true) }
                    var deleteButtonEnabled by remember { mutableStateOf(true) }

                    TextButton(
                        enabled = updateAvailable && updateButtonEnabled,
                        onClick = doAsync {
                            updateButtonEnabled = false
                            onUpdateRequested()
                            updateButtonEnabled = true
                        },
                    ) {
                        Text("Check for updates")
                    }
                    TextButton(
                        enabled = deleteButtonEnabled,
                        onClick = { showDeletionDialog = true },
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}

@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
@Preview
fun ModItemPreview() {
    ModItem(ModMeta.SampleModrinth, true, {}, {})
}
