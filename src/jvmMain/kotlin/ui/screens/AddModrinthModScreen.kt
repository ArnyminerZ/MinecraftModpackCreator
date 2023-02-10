package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import data.modrinth.Category
import data.modrinth.ModrinthCategoriesCache
import data.modrinth.ProjectType
import data.modrinth.SearchResult
import data.packwiz.Project
import kotlin.math.ceil
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import system.Modrinth
import ui.components.DropdownMenuCompat
import ui.components.DropdownMenuItemCompat
import ui.components.ModSearchItem
import utils.async
import utils.doAsync

context(Project, SnackbarHostState)
@ExperimentalComposeUiApi
@ExperimentalSerializationApi
@ExperimentalMaterial3Api
@Composable
fun AddModrinthModScreen(onModAdded: () -> Unit) {
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        val enabledCategories = mutableStateListOf<Category>()
        var categories by remember { mutableStateOf(emptyList<Category>()) }
        var query by remember { mutableStateOf("") }
        var search: SearchResult? by remember { mutableStateOf(null) }

        suspend fun performSearch(page: Int = 0) {
            println("Running search for \"$query\" with ${enabledCategories.size} categories. Page: $page")
            Modrinth.search(query, enabledCategories, page = page, projectType = ProjectType.mod).let { results ->
                println("Got ${results.hits.size} results of ${results.totalHits}")
                search = results
            }
        }
        LaunchedEffect(Unit) {
            async { performSearch() }
        }

        LaunchedEffect(Unit) {
            ModrinthCategoriesCache()
                .getOrFetch()
                .filter { it.projectType == "mod" }
                .let { categories = it }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    query,
                    { query = it },
                    Modifier.weight(1f),
                    singleLine = true,
                    maxLines = 1,
                    placeholder = { Text("Search...") },
                    trailingIcon = {
                        if (query.isNotEmpty())
                            IconButton(
                                onClick = { query = "" },
                            ) { Icon(Icons.Rounded.Close, "Clear") }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions { async { performSearch() } },
                )

                IconButton(
                    onClick = doAsync { performSearch() },
                ) { Icon(Icons.Rounded.Search, "Search") }

                Box {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { expanded = !expanded },
                    ) { Icon(Icons.Rounded.FilterAlt, "Categories") }
                    DropdownMenuCompat(expanded, { expanded = false }) {
                        for (category in categories) {
                            DropdownMenuItemCompat(
                                category.name,
                                leadingIcon = if (enabledCategories.isEmpty())
                                    null
                                else if (enabledCategories.contains(category))
                                    Icons.Rounded.CheckBox
                                else
                                    Icons.Rounded.CheckBoxOutlineBlank,
                            ) {
                                if (enabledCategories.contains(category)) enabledCategories.remove(category)
                                else enabledCategories.add(category)
                            }
                        }
                    }
                }
            }
        }

        val modsListState = rememberLazyListState()

        LazyColumn(state = modsListState, modifier = Modifier.fillMaxWidth().weight(1f)) {
            item {
                AnimatedVisibility(search == null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            items(search?.hits ?: emptyList()) { project -> ModSearchItem(project, onModAdded) }
        }

        search?.let { result ->
            val total = result.totalHits
            val offset = result.offset
            val limit = result.limit
            val pages = ceil(total.toFloat() / limit).toInt()
            val page = offset / limit

            val state = rememberLazyListState()
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(
                    { scope.launch { state.animateScrollBy(-150f) } },
                    Modifier.padding(horizontal = 4.dp),
                    enabled = state.layoutInfo.visibleItemsInfo.takeIf { it.isNotEmpty() }?.first()?.let {
                        it.index != 0 || it.offset != 0
                    } ?: true,
                ) { Icon(Icons.Rounded.ChevronLeft, "Left") }

                LazyRow(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .onPointerEvent(PointerEventType.Scroll) {
                            scope.launch { state.scrollBy(it.changes.first().scrollDelta.y * 50) }
                        },
                ) {
                    items(pages) { index ->
                        OutlinedButton(
                            doAsync { performSearch(index); scope.launch { modsListState.scrollToItem(0) } },
                            Modifier.padding(horizontal = 4.dp),
                            colors = if (page == index) ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                            )
                            else ButtonDefaults.outlinedButtonColors()
                        ) { Text((index + 1).toString()) }
                    }
                }

                OutlinedButton(
                    { scope.launch { state.animateScrollBy(150f) } },
                    Modifier.padding(horizontal = 4.dp),
                    enabled = state.layoutInfo
                        .visibleItemsInfo
                        .takeIf { it.isNotEmpty() }
                        ?.let { it.last().index <= pages }
                        ?: false,
                ) { Icon(Icons.Rounded.ChevronRight, "Right") }
            }
        }
    }
}
