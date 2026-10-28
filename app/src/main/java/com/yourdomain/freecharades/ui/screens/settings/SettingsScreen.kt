package com.yourdomain.freecharades.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourdomain.freecharades.ui.components.*

@Composable
fun SettingsScreen(onBack:()->Unit,onDocument:(String)->Unit,onCredits:()->Unit) {
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  Column(Modifier.safeDrawingPadding()) {
   ScreenHeader("How to play & info",onBack)
   Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).widthIn(max=640.dp).align(Alignment.CenterHorizontally).padding(24.dp)) {
    BrandMark(Modifier.size(60.dp));Spacer(Modifier.height(16.dp))
    Text("Just add people.",style=MaterialTheme.typography.displaySmall)
    Text("One phone. Two ways to play. No connection needed.",Modifier.padding(top=10.dp,bottom=28.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
    Text("Charades",style=MaterialTheme.typography.headlineSmall)
    Text("Choose a deck and keep the picture to yourself. Act out the answer without speaking. Everyone else guesses.",Modifier.padding(top=8.dp,bottom=24.dp),style=MaterialTheme.typography.bodyLarge)
    Text("20 Questions",style=MaterialTheme.typography.headlineSmall)
    Text("The group gets up to 20 yes-or-no questions to work out the picture. Use the counter to keep track together. You decide the house rules.",Modifier.padding(top=8.dp,bottom=24.dp),style=MaterialTheme.typography.bodyLarge)
    Text("Make it your game",style=MaterialTheme.typography.headlineSmall)
    Text("Pick a deck, turn the phone sideways, and wait for 3, 2, 1. Tap + or − to count guesses. Next resets the counter and reveals a new picture after another countdown. There is no timer or automatic scoring.",Modifier.padding(top=8.dp,bottom=24.dp),style=MaterialTheme.typography.bodyLarge)
    HorizontalDivider();Spacer(Modifier.height(16.dp))
    InfoLink("Picture credits",onCredits)
    InfoLink("Privacy policy",{onDocument("privacy")})
    InfoLink("Terms of service",{onDocument("terms")})
    InfoLink("Open-source license",{onDocument("license")})
    InfoLink("Font licenses",{onDocument("fonts")})
    Spacer(Modifier.height(24.dp))
    Text("Charades · version 1.0",style=MaterialTheme.typography.titleSmall)
    Text("Made for game night. Every deck, picture, and font is included in the app. No accounts, ads, or analytics.",Modifier.padding(top=6.dp),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
   }
  }
 }
}
@Composable private fun InfoLink(text:String,onClick:()->Unit) {
 TextButton(onClick,Modifier.fillMaxWidth().heightIn(min=52.dp),contentPadding=PaddingValues(horizontal=0.dp)) {
  Text(text,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium);AppIcon(Glyph.ARROW)
 }
}
