package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class AddExpenseUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var addExpenseUseCase: AddExpenseUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        addExpenseUseCase = AddExpenseUseCase(repository)
    }

    @Test
    fun `valid expense is passed to repository`() = runTest {
        // Given
        val expense = Expense(
            id = "1",
            amount = 100.0,
            currency = "LKR",
            category = Category.FOOD,
            note = "Test",
            date = LocalDate.now(),
            createdAt = Instant.now()
        )

        coEvery { repository.addExpense(any()) } returns Unit

        // When
        addExpenseUseCase(expense)

        // Then
        coVerify(exactly = 1) { repository.addExpense(expense) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `amount zero throws IllegalArgumentException`() = runTest {
        // Given
        val expense = Expense(
            id = "1",
            amount = 0.0,
            currency = "LKR",
            category = Category.FOOD,
            note = "Test",
            date = LocalDate.now(),
            createdAt = Instant.now()
        )

        // When
        addExpenseUseCase(expense)

        // Then - exception is thrown
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative amount throws IllegalArgumentException`() = runTest {
        // Given
        val expense = Expense(
            id = "1",
            amount = -50.0,
            currency = "LKR",
            category = Category.FOOD,
            note = "Test",
            date = LocalDate.now(),
            createdAt = Instant.now()
        )

        // When
        addExpenseUseCase(expense)

        // Then - exception is thrown
    }

    @Test
    fun `valid expense with positive amount succeeds`() = runTest {
        // Given
        val expense = Expense(
            id = "1",
            amount = 250.50,
            currency = "LKR",
            category = Category.TRANSPORT,
            note = null,
            date = LocalDate.now(),
            createdAt = Instant.now()
        )

        coEvery { repository.addExpense(any()) } returns Unit

        // When
        addExpenseUseCase(expense)

        // Then
        coVerify { repository.addExpense(match { it.amount == 250.50 }) }
    }
}
