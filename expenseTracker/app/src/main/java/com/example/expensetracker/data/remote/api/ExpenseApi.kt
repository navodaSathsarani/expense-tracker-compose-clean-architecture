package com.example.expensetracker.data.remote.api

import com.example.expensetracker.data.remote.dto.ExpenseDto

interface ExpenseApi {
    suspend fun getExpenses(): List<ExpenseDto>
    suspend fun addExpense(expense: ExpenseDto)
    suspend fun deleteExpense(id: String)
}
