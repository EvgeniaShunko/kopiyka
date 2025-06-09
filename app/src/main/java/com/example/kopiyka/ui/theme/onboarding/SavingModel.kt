package com.example.kopiyka.ui.onboarding.models

import kotlinx.serialization.Serializable

@Serializable
data class SavingModel(
    val title: String,
    val subtitle: String,
    val description: String
)

@Serializable
data class SavingModelList(
    val models: List<SavingModel>
)
