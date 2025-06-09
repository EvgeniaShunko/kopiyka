package com.example.kopiyka.ui.allexpenses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kopiyka.data.local.ExpensesStorage
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase
import com.example.kopiyka.ui.expenses.ExpenseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AllExpensesViewModel(
    application: Application,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase
) : AndroidViewModel(application) {

    private val _selectedFilter = MutableStateFlow("Дата")

    private val _groupedExpenses = MutableStateFlow<Map<String, List<ExpenseItem>>>(emptyMap())
    val groupedExpenses: StateFlow<Map<String, List<ExpenseItem>>> = _groupedExpenses

    private val _filteredExpenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val filteredExpenses: StateFlow<List<ExpenseItem>> = _filteredExpenses

    private val _availableFilters = MutableStateFlow<List<String>>(listOf("Дата"))
    val availableFilters: StateFlow<List<String>> = _availableFilters

    init {
        loadAvailableCategories()
        loadExpenses()
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
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

            if (_selectedFilter.value == "Дата") {
                _groupedExpenses.value = items.groupBy { it.date }
                _filteredExpenses.value = emptyList()
            } else {
                _filteredExpenses.value = items.filter { it.category == _selectedFilter.value }
                _groupedExpenses.value = emptyMap()
            }
        }
    }

    private fun loadAvailableCategories() {
        val onboardingData = getOnboardingDataUseCase.invoke()
        _availableFilters.value = listOf("Дата") + onboardingData.categories.toList()
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
}