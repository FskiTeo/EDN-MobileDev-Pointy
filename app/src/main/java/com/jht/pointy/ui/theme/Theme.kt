package com.jht.pointy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary          = BluePrimary,
    onPrimary        = BlueOnPrimary,
    background       = OffWhite,
    onBackground     = Ink,
    surface          = White,
    onSurface        = Ink,
    onSurfaceVariant = Stone,
    outline          = StoneLight,
    error            = ErrorRed,
    onError          = OnErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary          = BluePrimary,
    onPrimary        = BlueOnPrimary,
    background       = DarkBg,
    onBackground     = White,
    surface          = DarkSurface,
    onSurface        = White,
    onSurfaceVariant = DarkStone,
    outline          = DarkStone,
    error            = ErrorRed,
    onError          = OnErrorRed
)

@Composable
fun PointyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}