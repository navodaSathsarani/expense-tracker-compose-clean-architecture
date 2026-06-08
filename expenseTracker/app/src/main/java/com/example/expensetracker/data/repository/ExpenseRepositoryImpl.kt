package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.entity.SyncStatus
import com.example.expensetracker.data.mapper.toDomain
import com.example.expensetracker.data.mapper.toDto
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.data.sync.ExpenseSyncManager
import com.example.expensetracker.data.sync.ExpenseSyncScheduler
import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.util.CategorySummaryCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseApi: ExpenseApi,
    private val syncManager: ExpenseSyncManager,
    private val syncScheduler: ExpenseSyncScheduler
) : ExpenseRepository {

    override fun getExpenses(): Flow<List<Expense>> {
        return expenseDao.getExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity(syncStatus = SyncStatus.PENDING_UPLOAD))

        try {
            expenseApi.addExpense(expense.toDto())
            expenseDao.updateSyncStatus(expense.id, SyncStatus.SYNCED)
        } catch (_: Exception) {
            syncScheduler.scheduleSync()
        }
    }

    override suspend fun deleteExpense(id: String) {
        val entity = expenseDao.getExpenseById(id) ?: return
        expenseDao.deleteExpense(id)

        if (entity.syncStatus == SyncStatus.PENDING_UPLOAD) {
            return
        }

        try {
            expenseApi.deleteExpense(id)
        } catch (_: Exception) {
            syncManager.queuePendingDelete(id)
        }
    }

    override fun getSummary(): Flow<List<CategorySummary>> {
        return getExpenses().map { expenses ->
            CategorySummaryCalculator.fromExpenses(expenses)
        }
    }

    override suspend fun refreshExpenses() {
        syncManager.syncPending()

        val remoteExpenses = expenseApi.getExpenses()
        val pendingDeleteIds = syncManager.getPendingDeleteIds()
        val pendingUploads = expenseDao.getPendingUploadExpenses()
        val pendingUploadIds = pendingUploads.map { it.id }.toSet()

        val remoteEntities = remoteExpenses
            .filter { it.id !in pendingDeleteIds && it.id !in pendingUploadIds }
            .map { it.toEntity() }

        expenseDao.clearExpenses()
        expenseDao.insertExpenses(remoteEntities + pendingUploads)
    }
}
