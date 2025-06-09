package com.example.kopiyka.ui.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChartLegend(slices: List<PieChartSlice>, modifier: Modifier = Modifier) {
    val grouped = slices.chunked(2)
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        grouped.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                row.forEach { slice ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawRect(color = slice.color)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(slice.label, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
