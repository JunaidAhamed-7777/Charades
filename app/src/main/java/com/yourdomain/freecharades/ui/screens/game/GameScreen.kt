package com.yourdomain.freecharades.ui.screens.game

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourdomain.freecharades.domain.game.GamePhase
import com.yourdomain.freecharades.ui.components.*
import com.yourdomain.freecharades.ui.theme.*
import kotlinx.coroutines.delay

private tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}

@Composable
fun GameScreen(categoryId: String, sessionId: String, onBack: () -> Unit,
               vm: GameViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current.activity()
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastInteraction by remember { mutableLongStateOf(android.os.SystemClock.elapsedRealtime()) }
    LaunchedEffect(categoryId, sessionId) { vm.prepare(categoryId, sessionId) }
    DisposableEffect(activity) {
        val original = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (original != null) activity.requestedOrientation = original
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) vm.refresh() }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(lastInteraction, state.phase) {
        if (state.phase == GamePhase.REVEAL) {
            val remaining = 180000L - (android.os.SystemClock.elapsedRealtime() - lastInteraction)
            if (remaining > 0) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                delay(remaining)
            }
        }
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    AppContent {
        ScreenHeader(state.category?.title ?: "Your deck", onBack, close = true)
        when (state.phase) {
            GamePhase.LOADING -> StatusContent("Getting your clues ready…", true)
            GamePhase.COUNTDOWN -> Column(Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(state.countdown.toString(), style = AppType.display)
                Text("Get ready", Modifier.padding(top = AppSpacing.sm), style = AppType.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            GamePhase.ERROR -> StatusContent(state.error ?: "This clue could not be opened.")
            GamePhase.EXHAUSTED -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text("That's the whole deck", style = AppType.title)
                Text("Head back for a fresh shuffle.",
                    Modifier.padding(top = AppSpacing.sm, bottom = AppSpacing.xl),
                    style = AppType.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                PrimaryAction("Back to deck", onBack)
            }
            GamePhase.REVEAL -> Row(Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    lastInteraction = android.os.SystemClock.elapsedRealtime()
                }
            }
                .padding(bottom = AppSpacing.md), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xl)) {
                BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
                    val clueWidth = maxWidth
                    val clueHeight = maxHeight
                    val measurer = rememberTextMeasurer()
                    val density = LocalDensity.current
                    val answer = state.word?.text.orEmpty()
                    val credit = state.word?.credit.orEmpty()
                    val textWidth = with(density) { maxWidth.roundToPx() }.coerceAtLeast(1)
                    val answerHeight = measurer.measure(answer, style = AppType.title,
                        constraints = Constraints(maxWidth = textWidth)).size.height
                    val creditHeight = measurer.measure(credit, style = AppType.label, maxLines = 2,
                        constraints = Constraints(maxWidth = textWidth)).size.height
                    val imageHeight = maxHeight - with(density) { (answerHeight + creditHeight).toDp() } - AppSpacing.md
                    val needsScroll = state.word?.imagePath?.isNotBlank() == true && imageHeight < AppGeometry.touchTarget * 2
                    val clueScroll = if (needsScroll) Modifier.verticalScroll(rememberScrollState()) else Modifier
                    Column(Modifier.fillMaxSize().then(clueScroll)) {
                    if (state.word?.imagePath?.isNotBlank() == true) {
                        val boundedHeight = if (needsScroll) clueHeight else imageHeight
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            // Both bounds participate: a fixed-ratio image never pushes controls off-screen.
                            AppImage(state.word?.imageUri, state.word?.text,
                                Modifier.width(minOf(clueWidth, boundedHeight * AppGeometry.imageAspectRatio)))
                        }
                        Text(answer, Modifier.padding(top = AppSpacing.sm), style = AppType.title)
                    } else {
                        Box(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center) {
                            Text(answer, style = AppType.display, textAlign = TextAlign.Center)
                        }
                    }
                    Text(credit, Modifier.padding(top = AppSpacing.xs),
                        style = AppType.label, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                Column(Modifier.width(AppGeometry.controlRailWidth).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        GuessCount(state.guessCount)
                        Text("GUESSES", style = AppType.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(Modifier.padding(top = AppSpacing.sm), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            AppIconAction(Glyph.MINUS, "Remove a guess", {
                                lastInteraction = android.os.SystemClock.elapsedRealtime()
                                vm.decrementGuesses()
                            }, enabled = state.guessCount > 0)
                            AppIconAction(Glyph.PLUS, "Add a guess", {
                                lastInteraction = android.os.SystemClock.elapsedRealtime()
                                vm.incrementGuesses()
                            })
                        }
                    }
                    PrimaryAction("Next", {
                        lastInteraction = android.os.SystemClock.elapsedRealtime()
                        vm.next()
                    }, Modifier.fillMaxWidth().padding(top = AppSpacing.md))
                }
            }
        }
    }
}

@Composable
private fun GuessCount(count: Int) {
    val text = count.toString()
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxWidth().height(AppGeometry.touchTarget), contentAlignment = Alignment.Center) {
        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }
        val style = listOf(AppType.display, AppType.title, AppType.body, AppType.label).firstOrNull {
            val size = measurer.measure(text, style = it, maxLines = 1, softWrap = false).size
            size.width <= availableWidth && size.height <= availableHeight
        } ?: AppType.label
        Text(text, Modifier.semantics { contentDescription = "$count guesses" },
            style = style, maxLines = 1, softWrap = false)
    }
}
