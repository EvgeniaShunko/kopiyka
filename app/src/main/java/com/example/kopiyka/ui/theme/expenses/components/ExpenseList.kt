package com.example.kopiyka.ui.expenses.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kopiyka.ui.expenses.ExpenseItem

@Composable
fun ExpenseList(
    expenses: List<ExpenseItem>,
    modifier: Modifier = Modifier,
    onLongPress: (ExpenseItem) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(expenses) { item ->
            ExpenseItemCard(
                item = item,
                onLongPress = { onLongPress(it) }
            )
        }
    }
}
