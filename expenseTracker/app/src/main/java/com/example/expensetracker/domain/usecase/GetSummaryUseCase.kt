package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<CategorySummary>> {
        return repository.getExpenses().map { expenses ->
            if (expenses.isEmpty()) {
                emptyList()
            } else {
                val totalAmount = expenses.sumOf { it.amount }

                Category.values().mapNotNull { category ->
                    val categoryExpenses = expenses.filter { it.category == category }
                    if (categoryExpenses.isNotEmpty()) {
                        val categoryTotal = categoryExpenses.sumOf { it.amount }
                        CategorySummary(
                            category = category,
                            total = categoryTotal,
                            count = categoryExpenses.size,
                            percentage = (categoryTotal / totalAmount) * 100
                        )
                    } else null
                }.sortedByDescending { it.total }
            }
        }
    }
}
