package com.example.kopiyka.ui.expenses

data class ExpenseItem(
    val id: Int,
    val name: String,
    val category: String,
    val amount: Double,
    val icon: String,
    val date: String
)

data class ExpensesState(
    val expenses: List<ExpenseItem> = emptyList(),
    val totalBudget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
