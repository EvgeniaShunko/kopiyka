package com.example.kopiyka.ui.budget

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopiyka.R
import com.example.kopiyka.ui.components.SleekSlider
import com.example.kopiyka.ui.onboarding.OnboardingViewModel
import com.example.kopiyka.ui.onboarding.components.DotsIndicator

@Composable
fun BudgetDistributionScreen(
    onStart: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    }

    val totalBudget = prefs.getInt("key_budget", 0)
    val model = prefs.getString("key_saving_model", "") ?: ""
    val categories = prefs.getStringSet("key_categories", emptySet())?.toList() ?: emptyList()

    val savingPercentage = when (model) {
        "Економна" -> 0.2
        "Ультимативна" -> 0.3
        else -> 0.1
    }

    val adjustedBudget = (totalBudget * (1 - savingPercentage)).toInt()

    val allocations = remember { mutableStateMapOf<String, Int>() }
    val totalAllocated by remember { derivedStateOf { allocations.values.sum() } }
    var remainingBudget by remember { mutableStateOf(adjustedBudget) }

    val animatedRemaining by animateIntAsState(
        targetValue = remainingBudget,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.nav_bar_background))
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Розподіли бюджет по категоріях",
                fontSize = 22.sp,
                color = colorResource(id = R.color.text_primary),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "$animatedRemaining ₴",
                fontSize = 34.sp,
                color = if (remainingBudget < 0)
                    MaterialTheme.colorScheme.error
                else
                    colorResource(id = R.color.text_primary),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { category ->
                val allocation = allocations[category] ?: 0
                val animatedAllocation by animateFloatAsState(
                    targetValue = allocation.toFloat(),
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 300f
                    )
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "$category — ${animatedAllocation.toInt()}%",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.text_primary)
                    )

                    SleekSlider(
                        value = allocation.toFloat(),
                        onValueChange = { newValue: Float ->
                            val updated = newValue.toInt()
                            val previous = allocations[category] ?: 0
                            val delta = updated - previous
                            val updatedTotal = allocations.values.sum() + delta

                            if (updatedTotal <= 100) {
                                allocations[category] = updated
                                viewModel.onAllocationChanged(category, updated)

                                val usedAmount = (adjustedBudget * updatedTotal) / 100
                                remainingBudget = adjustedBudget - usedAmount
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DotsIndicator(selectedIndex = 4)

            Text(
                text = "Почати",
                color = if (totalAllocated == 100)
                    colorResource(id = R.color.text_primary)
                else
                    colorResource(id = R.color.text_secondary),
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(
                        enabled = totalAllocated == 100,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        prefs.edit(commit = true) {
                            allocations.forEach { (key, percent) ->
                                putInt("allocation_$key", percent)
                            }
                            putBoolean("onboarding_completed", true)
                        }
                        onStart()
                    }
            )
        }
    }
}
