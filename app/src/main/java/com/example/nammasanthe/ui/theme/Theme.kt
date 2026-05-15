package com.example.nammasanthe.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = SantheGreen,
    onPrimary = Color.White,
    primaryContainer = SantheGreenLight,
    onPrimaryContainer = SantheGreenDark,
    secondary = SantheOrange,
    onSecondary = Color.White,
    secondaryContainer = SantheOrangeLight,
    onSecondaryContainer = SantheOrangeDark,
    error = SantheRed,
    background = SantheCream,
    onBackground = SantheCharcoal,
    surface = Color.White,
    onSurface = SantheCharcoal,
)

private val DarkColors = darkColorScheme(
    primary = SantheGreenLight,
    onPrimary = SantheGreenDark,
    secondary = SantheOrangeLight,
    onSecondary = SantheOrangeDark,
    error = SantheRed,
)

@Composable
fun NammasantheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // turn OFF dynamic so brand colors win
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}