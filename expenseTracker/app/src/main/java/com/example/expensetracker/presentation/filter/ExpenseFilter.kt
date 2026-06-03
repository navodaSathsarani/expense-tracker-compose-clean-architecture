package com.example.expensetracker.presentation.filter

import com.example.expensetracker.domain.model.Category
import java.time.LocalDate

data class ExpenseFilter(
    val category: Category? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
) {
    val isActive: Boolean =
        category != null || startDate != null || endDate != null
}
