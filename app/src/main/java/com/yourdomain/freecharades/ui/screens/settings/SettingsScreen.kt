package com.yourdomain.freecharades.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.*

@Composable
fun SettingsScreen(onBack: () -> Unit, onDocument: (String) -> Unit, onCredits: () -> Unit) {
    AppContent {
        ScreenHeader("How to play & info", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(top = AppSpacing.lg, bottom = AppSpacing.xl)) {
            Text("One phone. Two ways to play. No connection needed.", style = AppType.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Rule("Charades", "Choose a deck and keep the clue to yourself. Act out the answer without speaking. Everyone else guesses.")
            Rule("20 Questions", "Ask yes-or-no questions to work out the answer. Use the counter to keep track together. You decide the house rules.")
            Rule("Playing a deck", "Turn the phone sideways and wait for 3, 2, 1. Tap + or − to count guesses. Next resets the counter and reveals a new clue after another countdown. There is no timer or automatic scoring.")
            Spacer(Modifier.height(AppSpacing.xxl))
            InfoLink("Clue & image sources", onCredits)
            InfoLink("Privacy policy") { onDocument("privacy") }
            InfoLink("Terms of service") { onDocument("terms") }
            InfoLink("Open-source license") { onDocument("license") }
            InfoLink("Font licenses") { onDocument("fonts") }
            Text("Charades · version 1.0", Modifier.padding(top = AppSpacing.xxl), style = AppType.label)
            Text("Every deck, picture, and font is included. No accounts, ads, or analytics.",
                Modifier.padding(top = AppSpacing.sm), style = AppType.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Rule(title: String, text: String) {
    Text(title, Modifier.padding(top = AppSpacing.xxl), style = AppType.title)
    Text(text, Modifier.padding(top = AppSpacing.sm), style = AppType.body,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InfoLink(text: String, onClick: () -> Unit) {
    SecondaryAction(text, onClick, Modifier.fillMaxWidth().padding(bottom = AppSpacing.sm))
}
