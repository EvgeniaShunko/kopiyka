package com.example.kopiyka.ui.expenses.components

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.kopiyka.R
import com.example.kopiyka.data.local.ExpensesStorage
import kotlinx.coroutines.launch
import kotlin.math.abs
import com.example.kopiyka.domain.optimization.*
import com.example.kopiyka.ui.components.OptimizationDialog

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onOptimizationCompleted: () -> Unit,
    onExpenseAdded: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    val optimizationResult = remember { mutableStateOf<OptimizationResult?>(null) }
    val storedCategories = prefs.getStringSet("key_categories", null)?.toList()
    val categories = remember(storedCategories) {
        storedCategories?.map { it.trim() }?.filter { it.isNotBlank() }
            ?: listOf("Їжа", "Транспорт", "Зв'язок")
    }

    val extended = remember(categories) { List(999) { categories[it % categories.size] } }
    val centerIndex = extended.size / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = centerIndex)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val centeredItemIndex by remember {
        derivedStateOf { getCenteredItemIndex(listState) }
    }

    val selectedCategory = extended.getOrNull(centeredItemIndex) ?: categories[0]

    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AndroidView(
                    factory = { context ->
                        View(context).apply {
                            setBackgroundColor(Color.Transparent.toArgb())
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            setRenderEffect(
                                RenderEffect.createBlurEffect(40f, 40f, Shader.TileMode.CLAMP)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable { onDismiss() }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .noRippleClickable { onDismiss() }
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(colorResource(id = R.color.nav_bar_background))
                        .padding(24.dp)
                        .widthIn(max = 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Додати витрату",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorResource(id = R.color.text_primary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = if (it.length > 25)
                                "Та ти поему пишеш?"
                            else null
                        },
                        label = {
                            Text("Назва", color = colorResource(id = R.color.text_secondary))
                        },
                        isError = nameError != null,
                        supportingText = {
                            nameError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(id = R.color.text_secondary).copy(alpha = 0.1f),
                            unfocusedContainerColor = colorResource(id = R.color.text_secondary).copy(alpha = 0.05f),
                            focusedTextColor = colorResource(id = R.color.text_primary),
                            unfocusedTextColor = colorResource(id = R.color.text_primary),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = amount,
                        onValueChange = {
                            val digitsOnly = it.filter { char -> char.isDigit() }
                            amount = digitsOnly
                            amountError = if (digitsOnly.length > 7) "Цифри вже не вміщаються!" else null
                        },
                        label = {
                            Text("Сума", color = colorResource(id = R.color.text_secondary))
                        },
                        isError = amountError != null,
                        supportingText = {
                            amountError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Text("₴", color = colorResource(id = R.color.text_secondary), fontSize = 18.sp)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(id = R.color.text_secondary).copy(alpha = 0.1f),
                            unfocusedContainerColor = colorResource(id = R.color.text_secondary).copy(alpha = 0.05f),
                            focusedTextColor = colorResource(id = R.color.text_primary),
                            unfocusedTextColor = colorResource(id = R.color.text_primary),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        contentPadding = PaddingValues(horizontal = 64.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(extended) { index, category ->
                            val isSelected = index == centeredItemIndex

                            val animatedColor by animateColorAsState(
                                targetValue = if (isSelected)
                                    colorResource(id = R.color.text_primary)
                                else
                                    colorResource(id = R.color.text_secondary),
                                animationSpec = tween(durationMillis = 300),
                                label = "CategoryColor"
                            )

                            val animatedSize by animateFloatAsState(
                                targetValue = if (isSelected) 20f else 16f,
                                animationSpec = tween(durationMillis = 300),
                                label = "CategorySize"
                            )

                            val animatedFontWeight by animateFloatAsState(
                                targetValue = if (isSelected) 700f else 400f,
                                animationSpec = tween(durationMillis = 300),
                                label = "FontWeight"
                            )

                            Text(
                                text = category,
                                fontSize = animatedSize.sp,
                                fontWeight = FontWeight(animatedFontWeight.toInt()),
                                color = animatedColor,
                                modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(index)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull()

                            if (name.length > 25) {
                                nameError = "Та ти поему пишеш?"
                                return@Button
                            }
                            if (amount.length > 7) {
                                amountError = "Цифри вже не вміщаються!"
                                return@Button
                            }

                            if (parsedAmount != null) {
                                coroutineScope.launch {
                                    val result = tryOptimizeAndSaveExpense(
                                        context = context,
                                        title = name,
                                        amount = parsedAmount,
                                        category = selectedCategory
                                    )
                                    if (result != null && result.coveredAmount > 0) {
                                        optimizationResult.value = result
                                    } else {
                                        onExpenseAdded()
                                        onDismiss()
                                    }
                                }
                            }

                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.selected_option_border),
                            contentColor = colorResource(id = R.color.text_primary)
                        )
                    ) {
                        Text("Зберегти")
                    }
                }

                optimizationResult.value?.let {
                    OptimizationDialog(result = it) {
                        optimizationResult.value = null
                        onOptimizationCompleted()
                        onExpenseAdded()
                        onDismiss()
                    }
                }
            }
        }
    }
}

suspend fun tryOptimizeAndSaveExpense(
    context: Context,
    title: String,
    amount: Double,
    category: String
): OptimizationResult? {
    val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    val totalBudget = prefs.getInt("key_budget_adjusted", 0)

    val allocations = prefs.all
        .filterKeys { it.startsWith("allocation_") }
        .mapNotNull {
            val name = it.key.removePrefix("allocation_")
            val percent = when (val value = it.value) {
                is Float -> value
                is Int -> value.toFloat()
                else -> null
            }
            percent?.let { p -> name to p }
        }
        .toMap()

    val expenses = ExpensesStorage.getExpenses(context)
    val spentPerCategory = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }

    val allocatedPercent = allocations[category] ?: 0f
    val allocatedAmount = totalBudget * (allocatedPercent / 100f)
    val currentSpent = spentPerCategory[category] ?: 0.0
    val newTotalSpent = currentSpent + amount

    val excess = if (newTotalSpent > allocatedAmount) {
        (newTotalSpent - allocatedAmount).coerceAtMost(amount)
    } else 0.0

    val result = if (excess > 0.0) {
        val input = OptimizationInput(
            totalBudget = totalBudget,
            categoryAllocations = allocations,
            categorySpent = spentPerCategory,
            overspentCategory = category,
            overspentAmount = excess
        )

        val output = OptimizationEngine.redistributeExcess(input)

        prefs.edit().apply {
            output.updatedAllocations.forEach { (cat, newPercent) ->
                putFloat("allocation_$cat", newPercent)
            }
        }.apply()

        output
    } else null

    ExpensesStorage.addExpense(context, title, amount, category)
    return result
}


fun getCenteredItemIndex(listState: LazyListState): Int {
    val layoutInfo = listState.layoutInfo
    val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2

    var closestItemIndex = listState.firstVisibleItemIndex
    var smallestDistance = Int.MAX_VALUE

    layoutInfo.visibleItemsInfo.forEach { item ->
        val itemCenter = item.offset + item.size / 2
        val distance = abs(itemCenter - viewportCenter)
        if (distance < smallestDistance) {
            smallestDistance = distance
            closestItemIndex = item.index
        }
    }

    return closestItemIndex
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() })
    }
