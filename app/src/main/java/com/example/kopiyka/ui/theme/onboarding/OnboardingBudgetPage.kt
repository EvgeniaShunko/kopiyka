package com.example.kopiyka.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kopiyka.R

@Composable
fun OnboardingBudgetPage(
    budget: String,
    onBudgetChanged: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val items = remember { (1000..100000 step 1000).toList() } // 🔧 Обмеження до 100000
    val paddedItems = listOf(null, null) + items + listOf(null, null)

    var initialScrollDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val defaultBudget = budget.toIntOrNull() ?: 20000
        val index = defaultBudget / 1000
        listState.scrollToItem(index)
        onBudgetChanged(defaultBudget.toString())
        initialScrollDone = true
    }

    val centerIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex + 2 }
    }

    LaunchedEffect(centerIndex, initialScrollDone) {
        if (initialScrollDone) {
            paddedItems.getOrNull(centerIndex)?.let { value ->
                onBudgetChanged(value.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AttachMoney,
            contentDescription = null,
            tint = colorResource(id = R.color.text_primary),
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Який твій бюджет?",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.text_primary)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Це допоможе підібрати правильну модель заощаджень",
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.text_secondary),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Box(modifier = Modifier.height(240.dp)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(paddedItems.size) { index ->
                    val value = paddedItems[index]
                    if (value != null) {
                        val isSelected = index == centerIndex

                        val animatedSize by animateDpAsState(
                            targetValue = if (isSelected) 36.dp else 24.dp,
                            animationSpec = tween(300)
                        )

                        val animatedColor by animateColorAsState(
                            targetValue = if (isSelected)
                                colorResource(id = R.color.text_primary)
                            else
                                colorResource(id = R.color.text_secondary)
                        )

                        Text(
                            text = "${value}₴",
                            fontSize = animatedSize.value.sp,
                            color = animatedColor,
                            modifier = Modifier.padding(vertical = 8.dp) // ❌ без .clickable
                        )
                    } else {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }

            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    }
}

