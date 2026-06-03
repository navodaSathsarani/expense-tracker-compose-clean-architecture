package com.example.expensetracker.data.remote.api

import com.example.expensetracker.data.remote.dto.CategorySummaryDto
import com.example.expensetracker.data.remote.dto.ExpenseDto

/**
 * Network contract mirroring REST endpoints. Replace [MockExpenseDataSource] with Retrofit.
 *
 * - GET /api/expenses
 * - GET /api/expenses?category={cat}&from={date}&to={date}
 * - POST /api/expenses
 * - DELETE /api/expenses/{id}
 * - GET /api/expenses/summary
 */
interface ExpenseApi {
    suspend fun getExpenses(
        category: String? = null,
        from: String? = null,
        to: String? = null
    ): List<ExpenseDto>

    suspend fun addExpense(expense: ExpenseDto)

    suspend fun deleteExpense(id: String)

    suspend fun getSummary(): List<CategorySummaryDto>
}
