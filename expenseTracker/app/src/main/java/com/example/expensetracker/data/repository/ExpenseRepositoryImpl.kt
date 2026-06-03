package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.mapper.toDomain
import com.example.expensetracker.data.mapper.toDto
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseApi: ExpenseApi
) : ExpenseRepository {

    // Room is the Single Source of Truth (SSOT)
    override fun getExpenses(): Flow<List<Expense>> {
        return expenseDao.getExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        // Save to local database first
        expenseDao.insertExpense(expense.toEntity())

        // Sync to remote (fire and forget for simplicity)
        try {
            expenseApi.addExpense(expense.toDto())
        } catch (e: Exception) {
            // In production, implement proper sync mechanism
            // For now, local data persists even if remote fails
        }
    }

    override suspend fun deleteExpense(id: String) {
        // Delete from local database first
        expenseDao.deleteExpense(id)

        // Sync to remote (fire and forget for simplicity)
        try {
            expenseApi.deleteExpense(id)
        } catch (e: Exception) {
            // In production, implement proper sync mechanism
        }
    }

    override fun getSummary(): Flow<List<CategorySummary>> {
        // Summary is calculated in the use case layer from expenses
        // This method exists for interface compliance but isn't used directly
        throw UnsupportedOperationException("Summary is calculated in GetSummaryUseCase")
    }

    override suspend fun refreshExpenses() {
        try {
            // Fetch from remote API
            val remoteExpenses = expenseApi.getExpenses()

            // Clear local cache
            expenseDao.clearExpenses()

            // Insert fresh data from remote
            val entities = remoteExpenses.map { it.toEntity() }
            expenseDao.insertExpenses(entities)
        } catch (e: Exception) {
            // If refresh fails, local data remains unchanged
            // In production, handle errors more gracefully
            throw e
        }
    }
}
