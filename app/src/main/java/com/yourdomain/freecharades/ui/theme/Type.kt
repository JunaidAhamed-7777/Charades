package com.yourdomain.freecharades.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yourdomain.freecharades.R
val PlayfulFont = FontFamily(Font(R.font.fredoka), Font(R.font.fredoka, FontWeight.Bold))
val BodyFont = FontFamily(Font(R.font.nunito_sans), Font(R.font.nunito_sans, FontWeight.Bold))
val Typography = Typography(
 displayLarge = TextStyle(PlayfulFont, FontWeight.Bold, 56.sp, lineHeight = 58.sp),
 displayMedium = TextStyle(PlayfulFont, FontWeight.Bold, 44.sp, lineHeight = 48.sp),
 displaySmall = TextStyle(PlayfulFont, FontWeight.Bold, 36.sp, lineHeight = 40.sp),
 headlineLarge = TextStyle(PlayfulFont, FontWeight.Bold, 32.sp, lineHeight = 36.sp),
 headlineMedium = TextStyle(PlayfulFont, FontWeight.Bold, 28.sp, lineHeight = 32.sp),
 headlineSmall = TextStyle(PlayfulFont, FontWeight.Bold, 24.sp, lineHeight = 29.sp),
 titleLarge = TextStyle(BodyFont, FontWeight.Bold, 22.sp, lineHeight = 28.sp),
 titleMedium = TextStyle(BodyFont, FontWeight.Bold, 17.sp, lineHeight = 24.sp),
 titleSmall = TextStyle(BodyFont, FontWeight.Bold, 15.sp, lineHeight = 21.sp),
 bodyLarge = TextStyle(BodyFont, FontWeight.Normal, 16.sp, lineHeight = 25.sp),
 bodyMedium = TextStyle(BodyFont, FontWeight.Normal, 14.sp, lineHeight = 21.sp),
 bodySmall = TextStyle(BodyFont, FontWeight.Normal, 12.sp, lineHeight = 18.sp),
 labelLarge = TextStyle(BodyFont, FontWeight.Bold, 16.sp, lineHeight = 22.sp),
 labelMedium = TextStyle(BodyFont, FontWeight.Bold, 13.sp, lineHeight = 18.sp),
 labelSmall = TextStyle(BodyFont, FontWeight.Bold, 11.sp, lineHeight = 15.sp)
)
