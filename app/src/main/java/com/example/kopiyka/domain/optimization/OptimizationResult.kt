package com.example.kopiyka.domain.optimization

data class OptimizationResult(
    val updatedAllocations: Map<String, Float>,
    val coveredAmount: Double,
    val uncoveredAmount: Double,
    val originalAllocations: Map<String, Float>,
    val totalBudget: Int

)
