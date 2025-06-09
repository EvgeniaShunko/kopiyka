package com.example.kopiyka.domain.optimization

data class OptimizationInput(
    val totalBudget: Int,
    val categoryAllocations: Map<String, Float>,
    val categorySpent: Map<String, Double>,
    val overspentCategory: String,
    val overspentAmount: Double
)
