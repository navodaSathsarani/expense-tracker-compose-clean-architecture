package com.example.expensetracker.domain.util

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.model.Expense

object CategorySummaryCalculator {
    fun fromExpenses(expenses: List<Expense>): List<CategorySummary> {
        if (expenses.isEmpty()) return emptyList()

        val totalAmount = expenses.sumOf { it.amount }
        return Category.entries.mapNotNull { category ->
            val categoryExpenses = expenses.filter { it.category == category }
            if (categoryExpenses.isEmpty()) return@mapNotNull null

            val categoryTotal = categoryExpenses.sumOf { it.amount }
            CategorySummary(
                category = category,
                total = categoryTotal,
                count = categoryExpenses.size,
                percentage = (categoryTotal / totalAmount) * 100
            )
        }.sortedByDescending { it.total }
    }
}
