package com.example.kopiyka.domain.optimization

object GreedyOptimizer {
    fun run(input: OptimizationInput): OptimizationResult {
        val totalBudget = input.totalBudget.toFloat()
        val allocations = input.categoryAllocations.toMutableMap()
        val spent = input.categorySpent
        val overspent = input.overspentCategory
        val overspentAmount = input.overspentAmount.toFloat()

        val candidates = allocations
            .filter { it.key != overspent }
            .mapNotNull { (category, percent) ->
                val allocatedAmount = (percent / 100f) * totalBudget
                val spentAmount = spent[category]?.toFloat() ?: 0f
                val remaining = allocatedAmount - spentAmount
                if (remaining > 0f) Triple(category, percent, remaining) else null
            }

        if (candidates.isEmpty()) {
            return OptimizationResult(
                updatedAllocations = allocations,
                coveredAmount = 0.0,
                uncoveredAmount = overspentAmount.toDouble(),
                originalAllocations = input.categoryAllocations,
                totalBudget = input.totalBudget
            )
        }

        val topCategories = candidates
            .sortedByDescending { it.third.toDouble() }
            .take(3)

        val totalRemaining = topCategories.sumOf { it.third.toDouble() }

        if (totalRemaining <= 0.0) {
            return OptimizationResult(
                updatedAllocations = allocations,
                coveredAmount = 0.0,
                uncoveredAmount = overspentAmount.toDouble(),
                originalAllocations = input.categoryAllocations,
                totalBudget = input.totalBudget
            )
        }

        val updated = allocations.toMutableMap()
        var covered = 0f

        for ((category, currentPercent, remaining) in topCategories) {
            val share = remaining / totalRemaining.toFloat()
            val toTakeAmount = (share * overspentAmount).coerceAtMost(remaining)
            val toTakePercent = (toTakeAmount / totalBudget) * 100f

            updated[category] = (currentPercent - toTakePercent).coerceAtLeast(0f)
            covered += toTakeAmount
        }

        return OptimizationResult(
            updatedAllocations = updated,
            coveredAmount = covered.toDouble(),
            uncoveredAmount = (overspentAmount - covered).coerceAtLeast(0f).toDouble(),
            originalAllocations = input.categoryAllocations,
            totalBudget = input.totalBudget
        )
    }
}
