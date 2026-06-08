package com.example.expensetracker.data.sync

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.dao.PendingDeleteDao
import com.example.expensetracker.data.local.entity.PendingDeleteEntity
import com.example.expensetracker.data.local.entity.SyncStatus
import com.example.expensetracker.data.mapper.toDomain
import com.example.expensetracker.data.mapper.toDto
import com.example.expensetracker.data.remote.api.ExpenseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseSyncManager @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val pendingDeleteDao: PendingDeleteDao,
    private val expenseApi: ExpenseApi,
    private val syncScheduler: ExpenseSyncScheduler
) {
    /**
     * Pushes pending uploads and deletes to the server.
     * @return true when every pending operation succeeded.
     */
    suspend fun syncPending(): Boolean {
        var allSucceeded = true

        for (expense in expenseDao.getPendingUploadExpenses()) {
            try {
                expenseApi.addExpense(expense.toDomain().toDto())
                expenseDao.updateSyncStatus(expense.id, SyncStatus.SYNCED)
            } catch (_: Exception) {
                allSucceeded = false
            }
        }

        for (id in pendingDeleteDao.getAllIds()) {
            try {
                expenseApi.deleteExpense(id)
                pendingDeleteDao.delete(id)
            } catch (_: Exception) {
                allSucceeded = false
            }
        }

        if (!allSucceeded) {
            syncScheduler.scheduleSync()
        }

        return allSucceeded
    }

    suspend fun queuePendingDelete(id: String) {
        pendingDeleteDao.insert(PendingDeleteEntity(id))
        syncScheduler.scheduleSync()
    }

    suspend fun getPendingDeleteIds(): Set<String> = pendingDeleteDao.getAllIds().toSet()
}
