package com.example.kopiyka.ui.allexpenses

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kopiyka.domain.usecase.GetOnboardingDataUseCase

class AllExpensesViewModelFactory(
    private val application: Application,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllExpensesViewModel::class.java)) {
            return AllExpensesViewModel(application, getOnboardingDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
