package com.yourdomain.freecharades.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val LightColors = lightColorScheme(
 primary=PartyBlue, onPrimary=Color.White, primaryContainer=Color(0xFFE8EDFF), onPrimaryContainer=Ink,
 secondary=Color(0xFF9D382B), onSecondary=Color.White, secondaryContainer=PartyCoral, onSecondaryContainer=Ink,
 tertiary=Success, background=Paper, onBackground=Ink, surface=Color.White, onSurface=Ink,
 surfaceVariant=Color(0xFFEDF0F8), onSurfaceVariant=MutedInk,
 outline=Color(0xFF748098), outlineVariant=Color(0xFFDDE2ED), error=Color(0xFFB3261E))
private val DarkColors = darkColorScheme(
 primary=Color(0xFFB6C4FF), onPrimary=Color(0xFF102675), primaryContainer=Color(0xFF263E93), onPrimaryContainer=Color(0xFFE2E8FF),
 secondary=PartyCoral, onSecondary=Ink, secondaryContainer=Color(0xFF763C35), onSecondaryContainer=Color(0xFFFFDAD3),
 tertiary=Color(0xFF8DD6B3), background=Night, onBackground=Color(0xFFF0F2FC), surface=NightSurface, onSurface=Color(0xFFF0F2FC),
 surfaceVariant=Color(0xFF25324D), onSurfaceVariant=Color(0xFFB6C1D7), outline=Color(0xFF8693AC), outlineVariant=Color(0xFF37445E))
@Composable fun FreeCharadesTheme(content: @Composable () -> Unit) {
 MaterialTheme(colorScheme=if(isSystemInDarkTheme()) DarkColors else LightColors,typography=Typography,shapes=Shapes,content=content)
}
