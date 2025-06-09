package com.example.kopiyka.ui.allexpenses

import com.example.kopiyka.ui.expenses.ExpenseItem

data class AllExpensesState(
    val groupedExpenses: Map<String, List<ExpenseItem>> = emptyMap(),
    val allExpenses: List<ExpenseItem> = emptyList()
)
