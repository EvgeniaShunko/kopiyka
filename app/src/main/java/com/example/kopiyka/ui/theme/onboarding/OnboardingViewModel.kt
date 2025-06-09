package com.example.kopiyka.ui.onboarding

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kopiyka.data.local.OnboardingData
import com.example.kopiyka.data.local.OnboardingPreferencesDataSource
import com.example.kopiyka.data.repository.OnboardingRepositoryImpl
import com.example.kopiyka.domain.usecase.SaveOnboardingDataUseCase
import kotlinx.coroutines.launch

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {

    var username by mutableStateOf("")
        private set

    var budget by mutableStateOf("19000")
        private set

    var savingModel by mutableStateOf("")
        private set

    val selectedCategories = mutableStateListOf<String>()

    val allocationMap = mutableStateMapOf<String, Float>()

    private val prefsSource = OnboardingPreferencesDataSource(app)
    private val repo        = OnboardingRepositoryImpl(prefsSource)
    private val saveUseCase = SaveOnboardingDataUseCase(repo)

    fun onUsernameChanged(new: String) {
        username = new
    }

    fun onBudgetChanged(new: String) {
        budget = new
    }

    fun onSavingModelSelected(model: String) {
        savingModel = model
    }

    fun onCategoryToggled(cat: String) {
        if (selectedCategories.contains(cat)) {
            selectedCategories.remove(cat)
            allocationMap.remove(cat)
        } else {
            selectedCategories.add(cat)
            allocationMap[cat] = 0f
        }
    }

    fun onAllocationChanged(category: String, percent: Int) {
        allocationMap[category] = percent.toFloat()
    }

    fun onContinue(onFinished: () -> Unit) {
        viewModelScope.launch {
            val originalBudget = budget.toIntOrNull() ?: 0

            val percentage = when (savingModel) {
                "Економна"     -> 0.2
                "Ультимативна" -> 0.3
                else           -> 0.1
            }

            val adjustedBudget = (originalBudget * (1 - percentage)).toInt()

            val data = OnboardingData(
                username       = username,
                budget         = originalBudget,
                adjustedBudget = adjustedBudget,
                savingModel    = savingModel,
                categories     = selectedCategories.toSet(),
                allocation     = allocationMap.toMap()
            )

            saveUseCase(data)

            onFinished()
        }
    }
}
