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
 displayLarge = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=56.sp, lineHeight = 58.sp),
 displayMedium = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=44.sp, lineHeight = 48.sp),
 displaySmall = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=36.sp, lineHeight = 40.sp),
 headlineLarge = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=32.sp, lineHeight = 36.sp),
 headlineMedium = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=28.sp, lineHeight = 32.sp),
 headlineSmall = TextStyle(fontFamily=PlayfulFont, fontWeight=FontWeight.Bold, fontSize=24.sp, lineHeight = 29.sp),
 titleLarge = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=22.sp, lineHeight = 28.sp),
 titleMedium = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=17.sp, lineHeight = 24.sp),
 titleSmall = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=15.sp, lineHeight = 21.sp),
 bodyLarge = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Normal, fontSize=16.sp, lineHeight = 25.sp),
 bodyMedium = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Normal, fontSize=14.sp, lineHeight = 21.sp),
 bodySmall = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Normal, fontSize=12.sp, lineHeight = 18.sp),
 labelLarge = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=16.sp, lineHeight = 22.sp),
 labelMedium = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=13.sp, lineHeight = 18.sp),
 labelSmall = TextStyle(fontFamily=BodyFont, fontWeight=FontWeight.Bold, fontSize=11.sp, lineHeight = 15.sp)
)
