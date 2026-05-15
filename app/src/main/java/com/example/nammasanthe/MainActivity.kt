package com.example.nammasanthe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nammasanthe.ui.theme.NammasantheTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Store.init(this)
        enableEdgeToEdge()
        setContent {
            NammasantheTheme(darkTheme = false) {  // force light
                AppNav()
            }
        }
    }
}