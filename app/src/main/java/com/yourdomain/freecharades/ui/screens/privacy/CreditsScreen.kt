package com.yourdomain.freecharades.ui.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.screens.menu.MenuViewModel

@Composable
fun CreditsScreen(onBack:()->Unit,vm:MenuViewModel=viewModel()) {
 val catalog by vm.catalog.collectAsStateWithLifecycle()
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  Column(Modifier.safeDrawingPadding()) {
   ScreenHeader("Picture credits",onBack)
   if(catalog.isLoading) StatusContent("Opening credits…",true)
   else if(catalog.error!=null) StatusContent(catalog.error!!,onRetry=vm::retry)
   else LazyColumn(Modifier.widthIn(max=720.dp).align(Alignment.CenterHorizontally),contentPadding=PaddingValues(24.dp),verticalArrangement=Arrangement.spacedBy(24.dp)) {
    item {Text("Real pictures. Proper credits.",style=MaterialTheme.typography.headlineMedium);Text("Image sources are included here for reference. All pictures are stored on your device.",Modifier.padding(top=8.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)}
    catalog.categories.forEach {category ->
     item {Text(category.title,style=MaterialTheme.typography.headlineSmall)}
     items(category.words,key={it.id}) {word ->
      Column {
       Text(word.text,style=MaterialTheme.typography.titleMedium)
       Text(word.credit,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
       Text(word.sourceUrl,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.primary)
      }
     }
    }
    item {Text("Brand artwork: original image generated for Charades. Typography: Fredoka by Milena Brandão and Nunito Sans by its contributors, distributed under the SIL Open Font License. Source and font license files are in the project repository.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
   }
  }
 }
}
