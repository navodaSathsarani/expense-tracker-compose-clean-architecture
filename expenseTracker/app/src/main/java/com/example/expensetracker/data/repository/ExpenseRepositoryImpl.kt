package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.mapper.toDomain
import com.example.expensetracker.data.mapper.toDto
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.util.CategorySummaryCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseApi: ExpenseApi
) : ExpenseRepository {

    override fun getExpenses(): Flow<List<Expense>> {
        return expenseDao.getExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())

        try {
            expenseApi.addExpense(expense.toDto())
        } catch (_: Exception) {
            // Local data persists when remote sync fails (offline-first).
        }
    }

    override suspend fun deleteExpense(id: String) {
        expenseDao.deleteExpense(id)

        try {
            expenseApi.deleteExpense(id)
        } catch (_: Exception) {
            // Local delete stands when remote sync fails.
        }
    }

    override fun getSummary(): Flow<List<CategorySummary>> {
        return getExpenses().map { expenses ->
            CategorySummaryCalculator.fromExpenses(expenses)
        }
    }

    override suspend fun refreshExpenses() {
        val remoteExpenses = expenseApi.getExpenses()
        expenseDao.clearExpenses()
        expenseDao.insertExpenses(remoteExpenses.map { it.toEntity() })
    }
}
