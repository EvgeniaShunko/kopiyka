package com.example.kopiyka.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DotsIndicator(
    selectedIndex: Int,
    totalDots: Int = 5
) {
    val selectedColor = Color.White
    val unselectedColor = Color(0x66FFFFFF)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { i ->
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (i == selectedIndex) 24.dp else 8.dp)
                    .background(
                        color = if (i == selectedIndex) selectedColor else unselectedColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}
