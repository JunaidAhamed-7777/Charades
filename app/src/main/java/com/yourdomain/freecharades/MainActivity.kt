package com.yourdomain.freecharades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import android.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.yourdomain.freecharades.ui.navigation.AppNavHost
import com.yourdomain.freecharades.ui.theme.FreeCharadesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle=SystemBarStyle.dark(Color.TRANSPARENT), navigationBarStyle=SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            FreeCharadesTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}