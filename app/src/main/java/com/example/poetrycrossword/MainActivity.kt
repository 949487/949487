package com.example.poetrycrossword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.poetrycrossword.ui.PoetryCrosswordApp
import com.example.poetrycrossword.ui.theme.PoetryCrosswordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoetryCrosswordTheme {
                PoetryCrosswordApp()
            }
        }
    }
}
