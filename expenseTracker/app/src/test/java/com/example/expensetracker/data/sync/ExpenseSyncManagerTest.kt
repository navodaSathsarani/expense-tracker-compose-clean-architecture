package com.example.expensetracker.data.sync

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.dao.PendingDeleteDao
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.local.entity.SyncStatus
import com.example.expensetracker.data.remote.api.ExpenseApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class ExpenseSyncManagerTest {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var pendingDeleteDao: PendingDeleteDao
    private lateinit var expenseApi: ExpenseApi
    private lateinit var syncScheduler: ExpenseSyncScheduler
    private lateinit var syncManager: ExpenseSyncManager

    private val pendingExpense = ExpenseEntity(
        id = "local-1",
        amount = 100.0,
        currency = "LKR",
        category = "FOOD",
        note = "Offline lunch",
        date = LocalDate.now(),
        createdAt = Instant.now(),
        syncStatus = SyncStatus.PENDING_UPLOAD
    )

    @Before
    fun setup() {
        expenseDao = mockk(relaxed = true)
        pendingDeleteDao = mockk(relaxed = true)
        expenseApi = mockk(relaxed = true)
        syncScheduler = mockk(relaxed = true)
        every { syncScheduler.scheduleSync() } just runs
        syncManager = ExpenseSyncManager(
            expenseDao = expenseDao,
            pendingDeleteDao = pendingDeleteDao,
            expenseApi = expenseApi,
            syncScheduler = syncScheduler
        )
    }

    @Test
    fun `syncPending uploads pending expenses and marks them synced`() = runTest {
        coEvery { expenseDao.getPendingUploadExpenses() } returns listOf(pendingExpense)
        coEvery { pendingDeleteDao.getAllIds() } returns emptyList()
        coEvery { expenseApi.addExpense(any()) } returns Unit
        coEvery { expenseDao.updateSyncStatus(any(), any()) } returns Unit

        val result = syncManager.syncPending()

        assertTrue(result)
        coVerify { expenseApi.addExpense(any()) }
        coVerify { expenseDao.updateSyncStatus("local-1", SyncStatus.SYNCED) }
        verify(exactly = 0) { syncScheduler.scheduleSync() }
    }

    @Test
    fun `syncPending schedules retry when upload fails`() = runTest {
        coEvery { expenseDao.getPendingUploadExpenses() } returns listOf(pendingExpense)
        coEvery { pendingDeleteDao.getAllIds() } returns emptyList()
        coEvery { expenseApi.addExpense(any()) } throws RuntimeException("offline")

        val result = syncManager.syncPending()

        assertFalse(result)
        verify { syncScheduler.scheduleSync() }
    }

    @Test
    fun `syncPending processes pending deletes`() = runTest {
        coEvery { expenseDao.getPendingUploadExpenses() } returns emptyList()
        coEvery { pendingDeleteDao.getAllIds() } returns listOf("server-1")
        coEvery { expenseApi.deleteExpense("server-1") } returns Unit
        coEvery { pendingDeleteDao.delete("server-1") } returns Unit

        val result = syncManager.syncPending()

        assertTrue(result)
        coVerify { expenseApi.deleteExpense("server-1") }
        coVerify { pendingDeleteDao.delete("server-1") }
    }
}
