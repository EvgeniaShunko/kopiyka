package com.example.kopiyka.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)
