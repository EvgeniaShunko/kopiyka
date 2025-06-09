package com.example.kopiyka.data.local

import android.content.Context
import com.example.kopiyka.data.model.Expense
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExpensesStorage {
    private const val FILE_NAME = "expenses.json"

    private val json = Json { prettyPrint = true }

    fun getExpenses(context: Context): List<Expense> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        return try {
            val content = file.readText()
            json.decodeFromString(content)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addExpense(context: Context, title: String, amount: Double, category: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val newExpense = Expense(title, amount, category, currentDate)

        val expenses = getExpenses(context).toMutableList()
        expenses.add(0, newExpense)

        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(expenses))
    }

    fun deleteExpense(context: Context, title: String, category: String, date: String) {
        val expenses = getExpenses(context).toMutableList()
        val updated = expenses.filterNot {
            it.title == title && it.category == category && it.date == date
        }

        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(updated))
    }
}
