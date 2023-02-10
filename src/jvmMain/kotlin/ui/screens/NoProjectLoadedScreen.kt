package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import system.storage.Config
import system.storage.ConfigKey

/**
 * Shown when no project has been loaded, and shows some default actions as quick accesses.
 */
@Preview
@Composable
@ExperimentalMaterial3Api
fun NoProjectLoadedScreen() {
    val recentProjects by Config.get().state<List<String>>(ConfigKey.RecentProjects)

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card {
            Text(
                "Welcome!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
            )
            ElevatedCard(
                Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    "Recent Projects",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                LazyColumn {
                    items(recentProjects ?: emptyList()) {

                    }
                }
            }
        }
    }
}
