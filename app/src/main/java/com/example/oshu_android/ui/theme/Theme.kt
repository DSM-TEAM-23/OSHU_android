package com.example.oshu_android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OshuColorScheme = lightColorScheme(
    primary = OshuPink,
    onPrimary = OshuWhite,
    background = OshuPinkLight,
    onBackground = OshuTextPrimary,
    surface = OshuWhite,
    onSurface = OshuTextPrimary,
    onSurfaceVariant = OshuHint,
    outline = OshuBorder,
    error = OshuError,
    onError = OshuWhite,
)

@Composable
fun OSHUAndroidTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OshuColorScheme,
        typography = Typography,
        content = content,
    )
}