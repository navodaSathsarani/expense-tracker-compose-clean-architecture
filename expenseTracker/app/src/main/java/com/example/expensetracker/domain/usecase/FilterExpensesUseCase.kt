package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class FilterExpensesUseCase @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) {
    operator fun invoke(
        category: Category? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Flow<List<Expense>> {
        return getExpensesUseCase().map { expenses ->
            expenses.filter { expense ->
                val matchesCategory = category == null || expense.category == category
                val matchesDateRange =
                    (startDate == null || !expense.date.isBefore(startDate)) &&
                        (endDate == null || !expense.date.isAfter(endDate))

                val hasCategoryFilter = category != null
                val hasDateFilter = startDate != null || endDate != null

                when {
                    hasCategoryFilter && hasDateFilter -> matchesCategory || matchesDateRange
                    else -> matchesCategory && matchesDateRange
                }
            }
        }
    }
}
