package com.example.kopiyka.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kopiyka.R
import com.example.kopiyka.domain.optimization.OptimizationResult

@Composable
fun OptimizationDialog(
    result: OptimizationResult,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = colorResource(id = R.color.nav_bar_background)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Увага",
                    tint = colorResource(id = R.color.text_primary),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Йойки, бюджет перевищено!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorResource(id = R.color.text_primary),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ми внесли зміни, щоб все врівноважити",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = colorResource(id = R.color.text_primary),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    result.updatedAllocations.forEach { (category, newPercent) ->
                        val oldPercent = result.originalAllocations[category] ?: return@forEach
                        val deltaPercent = oldPercent - newPercent
                        if (deltaPercent > 0f) {
                            val deltaAmount = (deltaPercent / 100f) * result.totalBudget
                            Text(
                                text = "- З категорії \"$category\" віднято ${deltaAmount.toInt()} ₴",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorResource(id = R.color.text_primary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = colorResource(id = R.color.text_primary)
                    )
                ) {
                    Text("Окей")
                }
            }
        }
    }
}
