package com.ezhart.eztodo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ezhart.eztodo.EZtodoApplication

@Composable
fun DynamicTheme(
    content: @Composable () -> Unit
){
    val app = LocalContext.current.applicationContext as EZtodoApplication
    val settingsRepository = app.settingsRepository

    val themeMode = settingsRepository.themeMode.collectAsStateWithLifecycle(ThemeMode.System)
    val useDynamicColors = settingsRepository.useDynamicColor.collectAsStateWithLifecycle(false)

    val shouldBeDark = when(themeMode.value){
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    AppTheme(shouldBeDark, useDynamicColors.value) { content() }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
