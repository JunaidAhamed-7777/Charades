package com.yourdomain.freecharades.ui.screens.categoryDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.*

@Composable
fun CategoryDetailScreen(categoryId: String, onBack: () -> Unit, onPlay: () -> Unit,
                         vm: CategoryDetailViewModel = viewModel()) {
    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val category = catalog.categories.firstOrNull { it.id == categoryId }
    AppContent {
        ScreenHeader(category?.title ?: "Your deck", onBack)
        when {
            catalog.isLoading -> StatusContent("Opening the deck…", true)
            category == null -> StatusContent("This deck could not be opened.", onRetry = vm::retry)
            else -> {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(top = AppSpacing.lg, bottom = AppSpacing.xl)) {
                    AppImage(category.imageUri, null, Modifier.fillMaxWidth())
                    Text("${category.words.size} clues · ready offline",
                        Modifier.padding(top = AppSpacing.xl), style = AppType.label,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(category.description, Modifier.padding(top = AppSpacing.md),
                        style = AppType.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Two ways to play", Modifier.padding(top = AppSpacing.xxl), style = AppType.title)
                    PlayRule("CHARADES", "Act out the answer without speaking. Everyone else guesses.")
                    PlayRule("20 QUESTIONS", "Ask yes-or-no questions to find the answer. Track guesses together.")
                    Text("Keep the clue to yourself. Use + and − to count guesses, then Next for another clue. Turn your phone sideways when play begins.",
                        Modifier.padding(top = AppSpacing.xl), style = AppType.body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                PrimaryAction("Play deck", onPlay, Modifier.fillMaxWidth()
                    .padding(vertical = AppSpacing.lg))
            }
        }
    }
}

@Composable
private fun PlayRule(title: String, description: String) {
    Column(Modifier.padding(top = AppSpacing.xl)) {
        Text(title, style = AppType.label)
        Text(description, Modifier.padding(top = AppSpacing.sm), style = AppType.body,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
