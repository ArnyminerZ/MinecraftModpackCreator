package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.apache.maven.artifact.versioning.ComparableVersion
import data.packwiz.Project

context(Project)
@ExperimentalMaterial3Api
@Composable
fun RowScope.ProjectGeneralPanel(onUpdateProject: (newProject: Project) -> Unit) {
    var newPack by remember { mutableStateOf(pack) }
    var saving by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(start = 4.dp, end = 8.dp)
            .padding(vertical = 8.dp)
            .weight(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "General Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                newPack.name,
                onValueChange = { newPack = newPack.copy(name = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text("Pack Name") },
                isError = newPack.name.isBlank(),
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions { },
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Sentences,
                    true,
                    KeyboardType.Text,
                    ImeAction.Next,
                ),
            )
            OutlinedTextField(
                newPack.author ?: "",
                onValueChange = { value -> newPack = newPack.copy(author = value.takeIf { it.isNotBlank() }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text("Pack Author") },
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions { },
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Sentences,
                    true,
                    KeyboardType.Text,
                    ImeAction.Next,
                ),
            )
            OutlinedTextField(
                newPack.version ?: "",
                onValueChange = { value -> newPack = newPack.copy(version = value.takeIf { it.isNotBlank() }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text("Pack Version") },
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions { },
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Sentences,
                    true,
                    KeyboardType.Text,
                    ImeAction.Next,
                ),
            )
            OutlinedTextField(
                newPack.description ?: "",
                onValueChange = { value -> newPack = newPack.copy(description = value.takeIf { it.isNotBlank() }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                label = { Text("Pack Description") },
                keyboardActions = KeyboardActions { },
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Sentences,
                    true,
                    KeyboardType.Text,
                    ImeAction.Next,
                ),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            AnimatedVisibility(pack != newPack) {
                OutlinedButton(
                    enabled = saving || pack.name.isNotBlank(),
                    onClick = {
                        saving = true
                        val saved = copy(pack = newPack)
                        onUpdateProject(saved.save())
                        saving = false
                    },
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
