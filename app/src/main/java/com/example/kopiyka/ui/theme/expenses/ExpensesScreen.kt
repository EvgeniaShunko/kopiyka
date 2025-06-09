package com.example.kopiyka.ui.expenses

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kopiyka.ui.analytics.AnalyticsViewModel
import com.example.kopiyka.ui.analytics.AnalyticsViewModelFactory
import com.example.kopiyka.ui.expenses.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExpensesScreen(
    expensesViewModel: ExpensesViewModel = viewModel()
) {
    val context = LocalContext.current
    val analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModelFactory(context.applicationContext as Application)
    )

    val state = expensesViewModel.uiState.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<ExpenseItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        expensesViewModel.loadExpenses()
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onOptimizationCompleted = {
                analyticsViewModel.refresh()
            },
            onExpenseAdded = {
                expensesViewModel.loadExpenses()
            }
        )
    }


    val visibleItems = remember(state.expenses) {
        mutableStateMapOf<Int, Boolean>().apply {
            state.expenses.take(5).forEach { put(it.id, true) }
        }
    }

    expenseToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = {
                Text(
                    text = "Видалити витрату?",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = "${item.name} просить залишити її в бюджеті 🙏",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseToDelete = null
                        coroutineScope.launch {
                            visibleItems[item.id] = false
                            delay(300)
                            expensesViewModel.deleteExpense(item)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("Так")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { expenseToDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("Ні")
                }
            },
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            BudgetSummary(
                total = state.totalBudget,
                remaining = state.remainingBudget
            )
            Spacer(Modifier.height(4.dp))
        }

        item {
            AddExpenseBlock(onClick = { showAddDialog = true })
            Spacer(Modifier.height(4.dp))
        }

        item {
            Text(
                text = "Останні витрати",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        val recentExpenses = state.expenses.take(5)

        if (recentExpenses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Тут може бути ваша перша витрата 💸",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(recentExpenses, key = { it.id }) { item ->
                val visible = visibleItems[item.id] ?: true

                AnimatedVisibility(
                    visible = visible,
                    exit = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it }
                ) {
                    ExpenseItemCard(
                        item = item,
                        onLongPress = {
                            expenseToDelete = item
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
