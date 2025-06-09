package com.example.kopiyka.data.local

import android.content.Context
import android.content.SharedPreferences

data class OnboardingData(
    val username: String,
    val budget: Int,
    val adjustedBudget: Int,
    val savingModel: String,
    val categories: Set<String>,
    val allocation: Map<String, Float> = emptyMap()
)

class OnboardingPreferencesDataSource(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    fun save(data: OnboardingData) {
        val editor = prefs.edit()

        val keysToRemove = prefs.all.keys.filter { it.startsWith("allocation_") }
        for (key in keysToRemove) {
            editor.remove(key)
        }

        editor.putString("key_username", data.username)
        editor.putInt("key_budget", data.budget)
        editor.putInt("key_budget_adjusted", data.adjustedBudget)
        editor.putString("key_saving_model", data.savingModel)
        editor.putStringSet("key_categories", data.categories)

        for ((category, percent) in data.allocation) {
            editor.putFloat("allocation_$category", percent)
        }

        editor.apply()
    }

    fun load(): OnboardingData {
        val username   = prefs.getString("key_username", "") ?: ""
        val budget     = prefs.getInt("key_budget", 0)
        val adjusted   = prefs.getInt("key_budget_adjusted", budget)
        val model      = prefs.getString("key_saving_model", "") ?: ""
        val categories = prefs.getStringSet("key_categories", emptySet()) ?: emptySet()

        val allocationMap = prefs.all
            .filter { it.key.startsWith("allocation_") }
            .mapNotNull {
                val key = it.key.removePrefix("allocation_")
                val value = it.value
                val floatValue = when (value) {
                    is Float -> value
                    is Int -> value.toFloat()
                    else -> null
                }
                if (floatValue != null) key to floatValue else null
            }
            .toMap()


        return OnboardingData(
            username        = username,
            budget          = budget,
            adjustedBudget  = adjusted,
            savingModel     = model,
            categories      = categories,
            allocation      = allocationMap
        )
    }
}
