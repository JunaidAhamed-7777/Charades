package com.yourdomain.freecharades.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MenuScreen(
    onCategoryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Main Menu",
            color = MaterialTheme.colorScheme.onBackground
        )
        // Temporary navigation buttons for testing
        Text(
            text = "Tap Animals (placeholder)",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onCategoryClick("animals") }
        )
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onSettingsClick() }
        )
        Text(
            text = "Privacy",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onPrivacyClick() }
        )
    }
}