package com.yourdomain.freecharades.ui.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yourdomain.freecharades.ui.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PrivacyPolicyScreen(onBack:()->Unit,document:String="privacy") {
 val context=LocalContext.current
 val file=when(document){"terms"->"terms_of_service.txt";"license"->"license.txt";"fonts"->"font_licenses.txt";else->"privacy_policy.txt"}
 val title=when(document){"terms"->"Terms of service";"license"->"Open-source license";"fonts"->"Font licenses";else->"Privacy policy"}
 var text by remember(file) {mutableStateOf<String?>(null)}
 LaunchedEffect(file) {
  text=withContext(Dispatchers.IO) {
   runCatching {context.assets.open(file).bufferedReader().use {it.readText()}}
    .getOrElse {"This document could not be opened. Please reinstall the complete APK."}
  }
 }
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  Column(Modifier.safeDrawingPadding()) {
   ScreenHeader(title,onBack)
   if(text==null) StatusContent("Opening document…",true)
   else Text(text!!,Modifier.weight(1f).verticalScroll(rememberScrollState()).widthIn(max=720.dp).align(Alignment.CenterHorizontally).padding(24.dp),style=MaterialTheme.typography.bodyLarge)
  }
 }
}
