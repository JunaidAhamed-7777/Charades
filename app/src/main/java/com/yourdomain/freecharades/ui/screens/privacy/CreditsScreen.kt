package com.yourdomain.freecharades.ui.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.screens.menu.MenuViewModel
import com.yourdomain.freecharades.ui.theme.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private data class CoverSource(val title: String, val sourceTitle: String, val credit: String, val url: String)

@Composable
fun CreditsScreen(onBack: () -> Unit, vm: MenuViewModel = viewModel()) {
    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var coverSources by remember { mutableStateOf<List<CoverSource>?>(null) }
    var sourceError by remember { mutableStateOf(false) }
    var sourceAttempt by remember { mutableIntStateOf(0) }
    LaunchedEffect(catalog.categories, sourceAttempt) {
        if (catalog.categories.isEmpty()) return@LaunchedEffect
        coverSources = null
        sourceError = false
        try {
            coverSources = withContext(Dispatchers.IO) {
                val covers = catalog.categories.associate { it.imagePath to it.title }
                val wordIds = catalog.categories.flatMap { it.words }.map { it.id }.toSet()
                val images = context.assets.open("photo_sources.json").bufferedReader().use {
                    JSONObject(it.readText()).getJSONArray("images")
                }
                buildList {
                    for (index in 0 until images.length()) {
                        val entry = images.getJSONObject(index)
                        val title = covers[entry.optString("imagePath")] ?: continue
                        if (entry.optString("id") !in wordIds) add(CoverSource(
                            "$title cover", entry.getString("sourceTitle"),
                            listOf(entry.optString("credit"), entry.optString("license"))
                                .filter { it.isNotBlank() }.joinToString(" · "), entry.getString("sourceUrl")))
                    }
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            sourceError = true
        }
    }
    AppContent {
        ScreenHeader("Clue & image sources", onBack)
        when {
            catalog.isLoading -> StatusContent("Opening sources…", true)
            catalog.error != null -> StatusContent(catalog.error!!, onRetry = vm::retry)
            else -> LazyColumn(contentPadding = PaddingValues(top = AppSpacing.lg, bottom = AppSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)) {
                item {
                    Text("Sources are included for reference. Every game image is stored on your device. Famous People uses names without photos.",
                        style = AppType.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                when {
                    sourceError -> item {
                        Column {
                            Text("Deck cover sources could not be opened. Try again or reinstall the complete APK.", style = AppType.body)
                            SecondaryAction("Try again", { sourceAttempt++ }, Modifier.padding(top = AppSpacing.md))
                        }
                    }
                    coverSources == null -> item {
                        Text("Opening deck cover sources…", style = AppType.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    else -> items(coverSources.orEmpty(), key = { it.title }) { source ->
                        Column {
                            Text(source.title, style = AppType.body)
                            Text(source.sourceTitle, Modifier.padding(top = AppSpacing.xs), style = AppType.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(source.credit, Modifier.padding(top = AppSpacing.xs), style = AppType.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(source.url, Modifier.padding(top = AppSpacing.xs), style = AppType.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                catalog.categories.forEach { category ->
                    item { Text(category.title, style = AppType.title) }
                    items(category.words, key = { it.id }) { word ->
                        Column {
                            Text(word.text, style = AppType.body)
                            Text(word.credit, Modifier.padding(top = AppSpacing.xs), style = AppType.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(word.sourceUrl, Modifier.padding(top = AppSpacing.xs), style = AppType.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item {
                    Text("Typography: Nunito Sans by its contributors, distributed under the SIL Open Font License. Source and font license files are included in the project repository.",
                        style = AppType.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
