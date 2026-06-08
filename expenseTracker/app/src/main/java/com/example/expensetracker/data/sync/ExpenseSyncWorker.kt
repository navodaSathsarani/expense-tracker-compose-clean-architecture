package com.example.expensetracker.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ExpenseSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: ExpenseSyncManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return if (syncManager.syncPending()) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
