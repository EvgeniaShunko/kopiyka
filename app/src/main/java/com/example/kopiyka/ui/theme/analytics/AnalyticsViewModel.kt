package com.example.kopiyka.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kopiyka.data.local.ExpensesStorage
import com.example.kopiyka.data.local.OnboardingPreferencesDataSource
import com.example.kopiyka.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategoryBalance(
    val category: String,
    val remaining: Int,
    val allocated: Int
)

class AnalyticsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = OnboardingPreferencesDataSource(app.applicationContext)

    private val _expenses = MutableStateFlow(ExpensesStorage.getExpenses(app.applicationContext))
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _balances = MutableStateFlow<List<CategoryBalance>>(emptyList())
    val balances: StateFlow<List<CategoryBalance>> = _balances

    init {
        loadBalances()
    }

    fun refresh() {
        _expenses.value = ExpensesStorage.getExpenses(getApplication())
        loadBalances()
    }

    private fun loadBalances() {
        viewModelScope.launch {
            val onboarding = prefs.load()
            val budget = onboarding.adjustedBudget
            val allocations = onboarding.allocation
            val categories = onboarding.categories

            val categorizedExpenses = _expenses.value.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val result = categories.map { category ->
                val percent = allocations[category] ?: 0f
                val allocated = ((percent / 100f) * budget).toInt()
                val spent = categorizedExpenses[category] ?: 0.0
                val remaining = (allocated - spent).toInt()
                CategoryBalance(category, remaining, allocated)
            }

            _balances.value = result
        }
    }
}
