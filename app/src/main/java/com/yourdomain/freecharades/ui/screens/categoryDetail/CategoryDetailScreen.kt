package com.yourdomain.freecharades.ui.screens.categoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onBack: () -> Unit,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Category: $categoryId",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Back (temporary)",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                text = "Play (temporary)",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onPlay() }
            )
        }
    }
}