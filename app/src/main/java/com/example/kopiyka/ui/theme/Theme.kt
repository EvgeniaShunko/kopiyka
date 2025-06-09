package com.example.kopiyka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.kopiyka.R


@Composable
fun KopiykaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = colorResource(id = R.color.selected_option_border),
        onPrimary = colorResource(id = R.color.text_primary),
        background = colorResource(id = R.color.nav_bar_background),
        onBackground = colorResource(id = R.color.text_primary),
        surface = colorResource(id = R.color.nav_bar_background),
        onSurface = colorResource(id = R.color.text_secondary)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}