package com.example.kopiyka.ui.expenses

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kopiyka.data.local.ExpensesStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ExpensesState())
    val uiState: StateFlow<ExpensesState> = _uiState

    private var nextId = 1

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val expenses = ExpensesStorage.getExpenses(context)

            val items = expenses.mapIndexed { index, expense ->
                ExpenseItem(
                    id = index + 1,
                    name = expense.title,
                    category = expense.category,
                    amount = expense.amount,
                    icon = categoryToEmoji(expense.category),
                    date = formatDate(expense.date)
                )
            }

            val totalBudget = getTotalBudgetFromPrefs(context)
            val spent = items.sumOf { it.amount }
            val remaining = totalBudget - spent

            _uiState.value = ExpensesState(
                expenses = items,
                totalBudget = totalBudget,
                remainingBudget = remaining,
                isLoading = false,
                errorMessage = null
            )

            nextId = items.size + 1
        }
    }

    fun deleteExpense(item: ExpenseItem) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            // Перетворимо дату назад до формату JSON
            val jsonDate = reverseFormatDate(item.date)
            ExpensesStorage.deleteExpense(
                context = context,
                title = item.name,
                category = item.category,
                date = jsonDate
            )
            loadExpenses()
        }
    }

    private fun formatDate(rawDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("uk"))
            outputFormat.format(date ?: Date())
        } catch (_: Exception) {
            rawDate
        }
    }

    private fun reverseFormatDate(formatted: String): String {
        return try {
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("uk"))
            val date = outputFormat.parse(formatted)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            inputFormat.format(date ?: Date())
        } catch (_: Exception) {
            formatted
        }
    }

    private fun categoryToEmoji(category: String): String {
        return when (category.lowercase(Locale.ROOT)) {
            "аренда" -> "🏠"
            "благодійність" -> "🙏"
            "догляд за собою" -> "🧖"
            "електроніка та гаджети" -> "💻"
            "зоотовари" -> "🐶"
            "інвестиції" -> "📈"
            "інтернет та мобільний зв'язок" -> "📶"
            "кафе й ресторани" -> "🍽"
            "комунальні послуги" -> "💡"
            "кредити" -> "💳"
            "медицина" -> "💊"
            "непередбачені витрати" -> "⚠️"
            "одяг і взуття" -> "👗"
            "освіта" -> "📚"
            "підписки" -> "📺"
            "побут і ремонт" -> "🔧"
            "подарунки" -> "🎁"
            "податки та штрафи" -> "📄"
            "подорожі та відпочинок" -> "✈️"
            "продукти" -> "🛒"
            "розваги та дозвілля" -> "🎉"
            "саморозвиток" -> "🌱"
            "страхування" -> "🛡️"
            "транспорт" -> "🚌"
            "хобі та творчість" -> "🎨"
            else -> "💸"
        }
    }


    private fun getTotalBudgetFromPrefs(context: Context): Double {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("key_budget_adjusted", 0).toDouble()
    }
}
