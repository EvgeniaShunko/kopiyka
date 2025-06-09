package com.example.kopiyka.domain.optimization

object OptimizationEngine {
    fun redistributeExcess(input: OptimizationInput): OptimizationResult {
        return GreedyOptimizer.run(input)
    }
}
