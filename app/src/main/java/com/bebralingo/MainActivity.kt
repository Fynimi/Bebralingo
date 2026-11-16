package com.bebralingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bebralingo.ui.theme.BebralingoTheme

/**
 * Точка входа в приложение.
 * Инициализирует тему и навигацию.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BebralingoTheme {
                com.bebralingo.navigation.AppNavigation()
            }
        }
    }
}
