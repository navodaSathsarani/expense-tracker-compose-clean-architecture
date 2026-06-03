package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteExpenseUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var deleteExpenseUseCase: DeleteExpenseUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        deleteExpenseUseCase = DeleteExpenseUseCase(repository)
    }

    @Test
    fun `correct id is passed to repository`() = runTest {
        // Given
        val expenseId = "test-id-123"
        coEvery { repository.deleteExpense(any()) } returns Unit

        // When
        deleteExpenseUseCase(expenseId)

        // Then
        coVerify { repository.deleteExpense(expenseId) }
    }

    @Test
    fun `repository deleteExpense is called exactly once`() = runTest {
        // Given
        val expenseId = "test-id-456"
        coEvery { repository.deleteExpense(any()) } returns Unit

        // When
        deleteExpenseUseCase(expenseId)

        // Then
        coVerify(exactly = 1) { repository.deleteExpense(expenseId) }
    }

    @Test
    fun `multiple delete calls result in multiple repository calls`() = runTest {
        // Given
        val expenseId1 = "id-1"
        val expenseId2 = "id-2"
        coEvery { repository.deleteExpense(any()) } returns Unit

        // When
        deleteExpenseUseCase(expenseId1)
        deleteExpenseUseCase(expenseId2)

        // Then
        coVerify(exactly = 1) { repository.deleteExpense(expenseId1) }
        coVerify(exactly = 1) { repository.deleteExpense(expenseId2) }
    }
}
