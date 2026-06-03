package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RefreshExpensesUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var refreshExpensesUseCase: RefreshExpensesUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        refreshExpensesUseCase = RefreshExpensesUseCase(repository)
    }

    @Test
    fun `invoke calls repository refreshExpenses once`() = runTest {
        refreshExpensesUseCase()

        coVerify(exactly = 1) { repository.refreshExpenses() }
    }
}
