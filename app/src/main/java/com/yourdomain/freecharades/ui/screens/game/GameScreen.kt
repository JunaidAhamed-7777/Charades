package com.yourdomain.freecharades.ui.screens.game

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yourdomain.freecharades.domain.game.GamePhase
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.PartyCoral
import kotlinx.coroutines.delay

private tailrec fun Context.activity():Activity? = when(this) {is Activity->this;is ContextWrapper->baseContext.activity();else->null}

@Composable
fun GameScreen(categoryId:String,sessionId:String,onBack:()->Unit,vm:GameViewModel=viewModel()) {
 val state by vm.state.collectAsStateWithLifecycle()
 val activity=LocalContext.current.activity()
 val lifecycleOwner=LocalLifecycleOwner.current
 var lastInteraction by remember {mutableLongStateOf(android.os.SystemClock.elapsedRealtime())}
 LaunchedEffect(categoryId,sessionId){vm.prepare(categoryId,sessionId)}
 DisposableEffect(activity) {
  val original=activity?.requestedOrientation
  activity?.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
  onDispose {activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);if(original!=null)activity.requestedOrientation=original}
 }
 DisposableEffect(lifecycleOwner) {
  val observer=LifecycleEventObserver {_,event->if(event==Lifecycle.Event.ON_RESUME)vm.refresh()}
  lifecycleOwner.lifecycle.addObserver(observer)
  onDispose {lifecycleOwner.lifecycle.removeObserver(observer)}
 }
 LaunchedEffect(lastInteraction,state.phase) {
  if(state.phase==GamePhase.REVEAL) {
   val remaining=180000L-(android.os.SystemClock.elapsedRealtime()-lastInteraction)
   if(remaining>0) {activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);delay(remaining)}
  }
  activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
 }
 Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background) {
  when(state.phase) {
   GamePhase.LOADING -> StatusContent("Getting your pictures ready…",true)
   GamePhase.COUNTDOWN -> Box(Modifier.fillMaxSize().background(Color.Black),contentAlignment=Alignment.Center) {
    Crossfade(state.countdown,label="Picture countdown") {number -> Text(number.toString(),style=MaterialTheme.typography.displayLarge.copy(fontSize=104.sp,lineHeight=112.sp),color=Color.White)}
   }
   GamePhase.ERROR -> Column(Modifier.safeDrawingPadding()) {ScreenHeader("Your deck",onBack);StatusContent(state.error ?: "This picture could not be opened.")}
   GamePhase.EXHAUSTED -> Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
    BrandMark(Modifier.size(64.dp));Spacer(Modifier.height(16.dp))
    Text("That's the whole deck!",style=MaterialTheme.typography.displaySmall)
    Text("Head back for a fresh shuffle.",Modifier.padding(top=8.dp,bottom=24.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
    PrimaryAction("Back to deck",onBack)
   }
   GamePhase.REVEAL -> Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal=16.dp).pointerInput(Unit) {
    awaitEachGesture {awaitFirstDown(requireUnconsumed=false);lastInteraction=android.os.SystemClock.elapsedRealtime()}
   }) {
    Row(Modifier.fillMaxWidth().heightIn(min=48.dp),verticalAlignment=Alignment.CenterVertically) {
     IconButton(onBack,Modifier.semantics {contentDescription="Leave game"}){AppIcon(Glyph.CLOSE)}
     Text(state.category?.title.orEmpty(),Modifier.padding(start=8.dp),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
     Spacer(Modifier.weight(1f))
     Text(state.word?.credit.orEmpty(),Modifier.widthIn(max=310.dp),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=2,overflow=TextOverflow.Ellipsis,textAlign=TextAlign.End)
    }
    Row(Modifier.weight(1f).fillMaxWidth().padding(bottom=12.dp),horizontalArrangement=Arrangement.spacedBy(20.dp)) {
     Column(Modifier.weight(1f)) {
      AsyncImage(state.word?.imageUri,state.word?.text,Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant),contentScale=ContentScale.Fit)
      Text(state.word?.text.orEmpty(),Modifier.padding(top=8.dp),style=MaterialTheme.typography.headlineLarge,maxLines=2,overflow=TextOverflow.Ellipsis)
     }
     BoxWithConstraints(Modifier.widthIn(min=110.dp,max=156.dp).fillMaxHeight()) {
      val compact=maxHeight<260.dp
      val addGuess={lastInteraction=android.os.SystemClock.elapsedRealtime();vm.incrementGuesses()}
      val removeGuess={lastInteraction=android.os.SystemClock.elapsedRealtime();vm.decrementGuesses()}
      Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally) {
       if(!compact) GuessButton(true,true,addGuess)
       GuessCount(state.guessCount)
       if(!compact) Text("guesses",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
       if(compact) Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        GuessButton(false,state.guessCount>0,removeGuess);GuessButton(true,true,addGuess)
       } else GuessButton(false,state.guessCount>0,removeGuess)
       Spacer(Modifier.weight(1f).heightIn(min=8.dp))
       PrimaryAction("Next",{lastInteraction=android.os.SystemClock.elapsedRealtime();vm.next()},Modifier.fillMaxWidth())
      }
     }
    }
   }
  }
 }
}

@Composable
private fun GuessCount(count:Int) {
 val text=count.toString()
 val style=MaterialTheme.typography.displayMedium
 val measurer=rememberTextMeasurer()
 val density=LocalDensity.current
 BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp),contentAlignment=Alignment.Center) {
  val measured=measurer.measure(text,style=style,maxLines=1,softWrap=false)
  val availableWidth=with(density){(maxWidth-8.dp).toPx()}
  val availableHeight=with(density){maxHeight.toPx()}
  val scale=minOf(1f,availableWidth/measured.size.width.coerceAtLeast(1),availableHeight/measured.size.height.coerceAtLeast(1))
  Text(text,Modifier.semantics {contentDescription="$count guesses"},style=style.copy(fontSize=style.fontSize*scale,lineHeight=style.lineHeight*scale),color=PartyCoral,maxLines=1,softWrap=false)
 }
}

@Composable
private fun GuessButton(add:Boolean,enabled:Boolean,onClick:()->Unit) {
 FilledTonalIconButton(onClick,Modifier.size(48.dp).semantics {contentDescription=if(add)"Add a guess" else "Remove a guess"},enabled=enabled) {
  AppIcon(if(add)Glyph.PLUS else Glyph.MINUS)
 }
}
