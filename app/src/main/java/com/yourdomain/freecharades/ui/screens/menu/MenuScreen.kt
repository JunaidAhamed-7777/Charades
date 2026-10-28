package com.yourdomain.freecharades.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yourdomain.freecharades.R
import com.yourdomain.freecharades.domain.model.Category
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.*

@Composable
fun MenuScreen(onCategoryClick:(String)->Unit,onSettingsClick:()->Unit,onPrivacyClick:()->Unit,vm:MenuViewModel=viewModel()) {
 val catalog by vm.catalog.collectAsStateWithLifecycle()
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  if(catalog.isLoading) StatusContent("Opening your picture cards…",true)
  else if(catalog.error!=null) StatusContent(catalog.error!!,onRetry=vm::retry)
  else LazyVerticalGrid(columns=GridCells.Adaptive(155.dp),contentPadding=PaddingValues(start=24.dp,end=24.dp,bottom=24.dp),
   horizontalArrangement=Arrangement.spacedBy(14.dp),verticalArrangement=Arrangement.spacedBy(14.dp),modifier=Modifier.safeDrawingPadding()) {
   item(span={GridItemSpan(maxLineSpan)}) {
    Row(Modifier.fillMaxWidth().padding(top=12.dp,bottom=8.dp),verticalAlignment=Alignment.CenterVertically) {
     BrandMark();Text("charades",Modifier.weight(1f).padding(start=8.dp),style=MaterialTheme.typography.headlineLarge)
     IconButton(onSettingsClick,Modifier.semantics {contentDescription="How to play and app information"}) {AppIcon(Glyph.INFO)}
    }
   }
   item(span={GridItemSpan(maxLineSpan)}) {
    Column(Modifier.fillMaxWidth().padding(bottom=6.dp)) {
     Text("Good company.\nGreat guesses.",style=MaterialTheme.typography.displayMedium)
     Text("The offline game of 20 Questions & Charades.",Modifier.padding(top=10.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
     Image(painterResource(R.drawable.game_night),null,Modifier.padding(top=22.dp).fillMaxWidth().heightIn(max=230.dp).aspectRatio(1.9f).clip(RoundedCornerShape(20.dp)),contentScale=ContentScale.Crop)
    }
   }
   item(span={GridItemSpan(maxLineSpan)}) {
    Row(Modifier.fillMaxWidth().padding(top=12.dp,bottom=2.dp),verticalAlignment=Alignment.CenterVertically) {
     Text("Pick your scene",Modifier.weight(1f),style=MaterialTheme.typography.headlineSmall)
     Text("${catalog.categories.size} decks",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
   }
   items(catalog.categories,key={it.id}) {category -> PictureDeck(category){onCategoryClick(category.id)}}
   item(span={GridItemSpan(maxLineSpan)}) {
    Column(Modifier.fillMaxWidth().padding(top=12.dp),horizontalAlignment=Alignment.CenterHorizontally) {
     Text("Every picture is already here. Just add people.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
     TextButton(onPrivacyClick){Text("Privacy policy")}
    }
   }
  }
 }
}

@Composable
private fun PictureDeck(category:Category,onClick:()->Unit) {
 Box(Modifier.fillMaxWidth().aspectRatio(.94f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
  .clickable(onClick=onClick).semantics(mergeDescendants=true){contentDescription="${category.title}, ${category.words.size} pictures. Open deck"}) {
  AsyncImage(category.imageUri,null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color(0xEE0B132A)),startY=100f)))
  Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
   Text(category.title,style=MaterialTheme.typography.titleMedium,color=Color.White)
   Text("${category.words.size} pictures",style=MaterialTheme.typography.bodySmall,color=Color(0xFFD7E0F7))
  }
 }
}
