package com.yourdomain.freecharades.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.domain.model.Category
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.*
import java.util.Locale

@Composable
fun MenuScreen(onCategoryClick: (String) -> Unit, onSettingsClick: () -> Unit,
               onPrivacyClick: () -> Unit, vm: MenuViewModel = viewModel()) {
    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val readableCardWidth = AppGeometry.deckMinWidth * LocalDensity.current.fontScale.coerceAtLeast(1f)
    AppContent {
        ScreenHeader("Charades") {
            AppIconAction(Glyph.INFO, "How to play and app information", onSettingsClick)
        }
        when {
            catalog.isLoading -> StatusContent("Opening your decks…", true)
            catalog.error != null -> StatusContent(catalog.error!!, onRetry = vm::retry)
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(readableCardWidth),
                contentPadding = PaddingValues(bottom = AppSpacing.xl),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm)) {
                        Text("Choose a deck", Modifier.semantics { heading() }, style = AppType.display)
                        Text("The offline game of 20 Questions & Charades.",
                            Modifier.padding(top = AppSpacing.sm), style = AppType.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(catalog.categories, key = { it.id }) { category ->
                    DeckCard(category) { onCategoryClick(category.id) }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(Modifier.fillMaxWidth().padding(top = AppSpacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                        Text("Made By Junaid", style = AppType.label,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        SecondaryAction("Privacy policy", onPrivacyClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckCard(category: Category, onClick: () -> Unit) {
    AppCard(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
        contentDescription = "${category.title}, ${category.words.size} clues. Open deck"
    }, onClick) {
        AppImage(category.imageUri, null, Modifier.fillMaxWidth()) {
            Box(Modifier.matchParentSize().background(AppColors.ImageScrim))
            Column(Modifier.align(Alignment.BottomStart).fillMaxWidth()
                .background(AppColors.ImageLabelScrim).padding(AppSpacing.md)) {
                Text(category.title.uppercase(Locale.ROOT), style = AppType.label)
                Text("${category.words.size} clues", Modifier.padding(top = AppSpacing.xs),
                    style = AppType.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
