package com.yourdomain.freecharades.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Privacy Policy",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}