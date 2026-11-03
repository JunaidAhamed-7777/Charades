package com.yourdomain.freecharades.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yourdomain.freecharades.R

@OptIn(ExperimentalTextApi::class)
private val AppFont = FontFamily(
    Font(R.font.nunito_sans, weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.nunito_sans, weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)))
)

/** Four styles, two weights. Material names are aliases, never separate type scales. */
object AppType {
    val display = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp, lineHeight = 48.sp)
    val title = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp)
    val body = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp)
    val label = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = .6.sp)
}

val Typography = Typography(
    displayLarge = AppType.display, displayMedium = AppType.display, displaySmall = AppType.display,
    headlineLarge = AppType.title, headlineMedium = AppType.title, headlineSmall = AppType.title,
    titleLarge = AppType.title, titleMedium = AppType.title, titleSmall = AppType.title,
    bodyLarge = AppType.body, bodyMedium = AppType.body, bodySmall = AppType.body,
    labelLarge = AppType.label, labelMedium = AppType.label, labelSmall = AppType.label
)
