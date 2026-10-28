package com.yourdomain.freecharades.ui.screens.categoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yourdomain.freecharades.ui.components.*

@Composable
fun CategoryDetailScreen(categoryId:String,onBack:()->Unit,onPlay:()->Unit,vm:CategoryDetailViewModel=viewModel()) {
 val catalog by vm.catalog.collectAsStateWithLifecycle()
 val category=catalog.categories.firstOrNull {it.id==categoryId}
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  Column(Modifier.safeDrawingPadding()) {
   ScreenHeader(category?.title ?: "Your deck",onBack)
   if(catalog.isLoading) StatusContent("Opening the deck…",true)
   else if(category==null) StatusContent("This deck could not be opened.",onRetry=vm::retry)
   else Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).widthIn(max=640.dp).align(Alignment.CenterHorizontally).padding(24.dp)) {
    AsyncImage(category.imageUri,null,Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(20.dp)),contentScale=ContentScale.Crop)
    Spacer(Modifier.height(24.dp))
    Text(category.title,style=MaterialTheme.typography.displaySmall)
    Text("${category.words.size} pictures · ready to play offline",Modifier.padding(top=6.dp),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary)
    Text(category.description,Modifier.padding(top=12.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(28.dp))
    Text("One deck. Two ways to play.",style=MaterialTheme.typography.headlineSmall)
    PlayRule("Charades","Act out the picture. No talking, just your best performance.")
    PlayRule("20 Questions","Let the group ask yes-or-no questions to find the answer.")
    Text("Keep the screen to yourself. Use + and − to count guesses, then Next for a new picture. Turn your phone sideways when play begins.",Modifier.padding(top=20.dp),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(28.dp))
    PrimaryAction("Let's play",onPlay,Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
   }
  }
 }
}
@Composable private fun PlayRule(title:String,description:String) {
 Row(Modifier.padding(top=18.dp),horizontalArrangement=Arrangement.spacedBy(14.dp)) {
  Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),contentAlignment=Alignment.Center){AppIcon(Glyph.CARDS,tint=MaterialTheme.colorScheme.onPrimaryContainer)}
  Column(Modifier.weight(1f)){Text(title,style=MaterialTheme.typography.titleMedium);Text(description,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
 }
}
