package com.yourdomain.freecharades.ui.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.screens.menu.MenuViewModel
import com.yourdomain.freecharades.ui.theme.*

@Composable
fun CreditsScreen(onBack: () -> Unit, vm: MenuViewModel = viewModel()) {
    val catalog by vm.catalog.collectAsStateWithLifecycle()
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
