package com.yourdomain.freecharades.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import com.yourdomain.freecharades.ui.theme.*

enum class Glyph { BACK, CLOSE, INFO, ARROW, PLUS, MINUS, CARDS }

@Composable
fun AppIcon(glyph: Glyph, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    Canvas(modifier.size(AppSpacing.xl)) {
        val w = size.width
        val h = size.height
        val stroke = w / 12f
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(tint, Offset(w*x1, h*y1), Offset(w*x2, h*y2), stroke, StrokeCap.Round)
        when (glyph) {
            Glyph.BACK -> { line(.78f,.5f,.2f,.5f); line(.2f,.5f,.48f,.22f); line(.2f,.5f,.48f,.78f) }
            Glyph.CLOSE -> { line(.25f,.25f,.75f,.75f); line(.25f,.75f,.75f,.25f) }
            Glyph.ARROW -> { line(.2f,.5f,.8f,.5f); line(.8f,.5f,.52f,.22f); line(.8f,.5f,.52f,.78f) }
            Glyph.PLUS -> { line(.2f,.5f,.8f,.5f); line(.5f,.2f,.5f,.8f) }
            Glyph.MINUS -> line(.2f,.5f,.8f,.5f)
            Glyph.INFO -> {
                drawCircle(tint, w*.36f, style=Stroke(stroke))
                line(.5f,.46f,.5f,.7f)
                drawCircle(tint, stroke*.6f, Offset(w*.5f,h*.3f))
            }
            Glyph.CARDS -> {
                drawRoundRect(tint, Offset(w*.12f,h*.12f), Size(w*.58f,h*.7f), CornerRadius(w*.12f), style=Stroke(stroke))
                drawRoundRect(tint, Offset(w*.32f,h*.22f), Size(w*.56f,h*.66f), CornerRadius(w*.12f), style=Stroke(stroke))
            }
        }
    }
}

/** One safe, centered content column and one horizontal inset on every route. */
@Composable
fun AppContent(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.TopCenter) {
            Column(modifier.fillMaxHeight().widthIn(max = AppGeometry.contentWidth)
                .fillMaxWidth().padding(horizontal = AppSpacing.xl), content = content)
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    close: Boolean = false,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(Modifier.fillMaxWidth().padding(vertical = AppSpacing.sm)
        .heightIn(min = AppGeometry.touchTarget), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            AppIconAction(if (close) Glyph.CLOSE else Glyph.BACK,
                if (close) "Leave game" else "Back", onBack)
        } else {
            Box(Modifier.size(AppGeometry.touchTarget), contentAlignment = Alignment.Center) {
                AppIcon(Glyph.CARDS, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Text(title, Modifier.weight(1f).padding(start = AppSpacing.sm), style = AppType.title)
        trailing()
    }
}

/** The single card surface; business variants provide their own content. */
@Composable
fun AppCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null,
            content: @Composable BoxScope.() -> Unit) {
    val action = if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick)
    Box(modifier.clip(AppShape).background(MaterialTheme.colorScheme.surface).then(action), content = content)
}

/** No caller can accidentally render an unbounded or natural-size image. */
@Composable
fun AppImage(model: Any?, description: String?, modifier: Modifier = Modifier,
             overlay: @Composable BoxScope.() -> Unit = {}) {
    Box(modifier.aspectRatio(AppGeometry.imageAspectRatio).clip(AppShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)) {
        AsyncImage(model, description, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        overlay()
    }
}

@Composable
fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
                  enabled: Boolean = true) {
    Button(onClick, modifier.heightIn(min = AppGeometry.touchTarget), enabled = enabled,
        shape = AppShape, elevation = null,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.md)) {
        Text(text, style = AppType.body)
        Spacer(Modifier.width(AppSpacing.md))
        AppIcon(Glyph.ARROW, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SecondaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
                    enabled: Boolean = true) {
    OutlinedButton(onClick, modifier.heightIn(min = AppGeometry.touchTarget), enabled = enabled,
        shape = AppShape, border = BorderStroke(AppGeometry.borderWidth, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.md)) {
        Text(text, style = AppType.body)
    }
}

@Composable
fun AppIconAction(glyph: Glyph, description: String, onClick: () -> Unit,
                  modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(modifier.size(AppGeometry.touchTarget).clip(AppShape)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .semantics { contentDescription = description }, contentAlignment = Alignment.Center) {
        AppIcon(glyph, tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun StatusContent(message: String, isLoading: Boolean = false, onRetry: (() -> Unit)? = null) {
    Column(Modifier.fillMaxSize().padding(vertical = AppSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(AppSpacing.xl))
        }
        Text(message, style = AppType.body)
        if (onRetry != null) {
            Spacer(Modifier.height(AppSpacing.xl))
            SecondaryAction("Try again", onRetry)
        }
    }
}
