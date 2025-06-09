package com.example.kopiyka.data.repository

import com.example.kopiyka.data.local.OnboardingData
import com.example.kopiyka.data.local.OnboardingPreferencesDataSource


interface OnboardingRepository {
    fun saveOnboarding(data: OnboardingData)
    fun getOnboarding(): OnboardingData
}


class OnboardingRepositoryImpl(
    private val local: OnboardingPreferencesDataSource
) : OnboardingRepository {
    override fun saveOnboarding(data: OnboardingData) = local.save(data)
    override fun getOnboarding(): OnboardingData    = local.load()
}
