package com.example.expensetracker.domain.repository

import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(id: String)
    fun getSummary(): Flow<List<CategorySummary>>
    suspend fun refreshExpenses()
}
