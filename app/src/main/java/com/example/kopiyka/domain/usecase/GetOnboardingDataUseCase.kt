package com.example.kopiyka.domain.usecase

import com.example.kopiyka.data.local.OnboardingData
import com.example.kopiyka.data.repository.OnboardingRepository


class GetOnboardingDataUseCase(
    private val repo: OnboardingRepository
) {
    operator fun invoke(): OnboardingData = repo.getOnboarding()
}
