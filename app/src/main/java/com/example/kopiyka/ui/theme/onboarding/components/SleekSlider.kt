package com.example.kopiyka.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.kopiyka.R

@Composable
fun SleekSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbColor = colorResource(id = R.color.text_primary)
    val activeTrackColor = thumbColor
    val inactiveTrackColor = thumbColor.copy(alpha = 0.3f)

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..100f,
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        )
    )
}
