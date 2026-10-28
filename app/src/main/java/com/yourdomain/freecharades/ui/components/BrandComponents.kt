package com.yourdomain.freecharades.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.yourdomain.freecharades.ui.theme.PartyBlue
import com.yourdomain.freecharades.ui.theme.PartyCoral

enum class Glyph { BACK, CLOSE, INFO, ARROW, PLUS, MINUS, CHECK, CARDS, HEART }

@Composable
fun AppIcon(glyph: Glyph, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    Canvas(modifier.size(24.dp)) {
        val w = size.width; val h = size.height; val stroke = 2.dp.toPx()
        fun line(x1: Float,y1: Float,x2: Float,y2: Float) = drawLine(tint,Offset(w*x1,h*y1),Offset(w*x2,h*y2),stroke,StrokeCap.Round)
        when(glyph) {
            Glyph.BACK -> { line(.78f,.5f,.2f,.5f);line(.2f,.5f,.48f,.22f);line(.2f,.5f,.48f,.78f) }
            Glyph.CLOSE -> {line(.25f,.25f,.75f,.75f);line(.25f,.75f,.75f,.25f)}
            Glyph.ARROW -> {line(.2f,.5f,.8f,.5f);line(.8f,.5f,.52f,.22f);line(.8f,.5f,.52f,.78f)}
            Glyph.PLUS -> {line(.2f,.5f,.8f,.5f);line(.5f,.2f,.5f,.8f)}
            Glyph.MINUS -> line(.2f,.5f,.8f,.5f)
            Glyph.CHECK -> {line(.2f,.52f,.42f,.74f);line(.42f,.74f,.82f,.26f)}
            Glyph.INFO -> {drawCircle(tint,w*.36f,style=Stroke(stroke));line(.5f,.46f,.5f,.7f);drawCircle(tint,stroke*.6f,Offset(w*.5f,h*.3f))}
            Glyph.CARDS -> {rotate(-12f){drawRoundRect(tint,Offset(w*.15f,h*.14f),Size(w*.57f,h*.7f),CornerRadius(3.dp.toPx()),style=Stroke(stroke))};drawRoundRect(tint,Offset(w*.33f,h*.22f),Size(w*.5f,h*.65f),CornerRadius(3.dp.toPx()),style=Stroke(stroke))}
            Glyph.HEART -> {val p=Path().apply {moveTo(w*.5f,h*.82f);cubicTo(0f,h*.52f,w*.08f,h*.06f,w*.5f,h*.31f);cubicTo(w*.92f,h*.06f,w,h*.52f,w*.5f,h*.82f)};drawPath(p,tint,style=Stroke(stroke))}
        }
    }
}

@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Canvas(modifier.size(42.dp)) {
        val w=size.width;val h=size.height
        rotate(-13f,Offset(w*.45f,h*.5f)) {drawRoundRect(PartyCoral,Offset(w*.12f,h*.14f),Size(w*.6f,h*.7f),CornerRadius(w*.13f))}
        rotate(9f,Offset(w*.6f,h*.5f)) {
            drawRoundRect(PartyBlue,Offset(w*.3f,h*.16f),Size(w*.6f,h*.7f),CornerRadius(w*.13f))
            val p=Path().apply {moveTo(w*.48f,h*.34f);lineTo(w*.74f,h*.51f);lineTo(w*.48f,h*.68f);close()}
            drawPath(p,Color.White)
        }
    }
}

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(start=12.dp,end=20.dp,top=8.dp,bottom=8.dp),verticalAlignment=Alignment.CenterVertically) {
        IconButton(onClick=onBack) {AppIcon(Glyph.BACK)}
        Text(title,Modifier.weight(1f).padding(start=8.dp),style=MaterialTheme.typography.titleLarge)
        trailing()
    }
}

@Composable
fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(onClick,modifier.heightIn(min=56.dp),enabled=enabled,shape=RoundedCornerShape(14.dp),
        colors=ButtonDefaults.buttonColors(containerColor=PartyBlue,contentColor=Color.White),contentPadding=PaddingValues(horizontal=22.dp,vertical=14.dp)) {
        Text(text,style=MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(12.dp));AppIcon(Glyph.ARROW)
    }
}

@Composable
fun StatusContent(message: String, isLoading: Boolean=false, onRetry: (() -> Unit)?=null) {
    Column(Modifier.fillMaxSize().padding(32.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
        if(isLoading) {CircularProgressIndicator();Spacer(Modifier.height(20.dp))}
        Text(message,style=MaterialTheme.typography.bodyLarge)
        if(onRetry!=null) {Spacer(Modifier.height(20.dp));OutlinedButton(onRetry){Text("Try again")}}
    }
}
