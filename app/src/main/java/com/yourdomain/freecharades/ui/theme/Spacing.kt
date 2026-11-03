package com.yourdomain.freecharades.ui.theme

import androidx.compose.ui.unit.dp

/** The spacing scale; dimensions below describe geometry rather than new spacing steps. */
object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object AppGeometry {
    val touchTarget = AppSpacing.xl * 2
    val contentWidth = AppSpacing.xxl * 20
    val deckMinWidth = AppSpacing.xxl * 5
    val controlRailWidth = AppSpacing.xxl * 5
    val compactGameHeight = AppSpacing.xxl * 8
    val borderWidth = AppSpacing.xs / 4
    const val imageAspectRatio = 4f / 3f
}
