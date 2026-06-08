package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class FilterExpensesUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var getExpensesUseCase: GetExpensesUseCase
    private lateinit var filterExpensesUseCase: FilterExpensesUseCase

    private val testExpenses = listOf(
        Expense(
            id = "1",
            amount = 100.0,
            currency = "LKR",
            category = Category.FOOD,
            note = "Lunch",
            date = LocalDate.of(2024, 1, 10),
            createdAt = Instant.now()
        ),
        Expense(
            id = "2",
            amount = 50.0,
            currency = "LKR",
            category = Category.TRANSPORT,
            note = "Bus",
            date = LocalDate.of(2024, 1, 15),
            createdAt = Instant.now()
        ),
        Expense(
            id = "3",
            amount = 200.0,
            currency = "LKR",
            category = Category.FOOD,
            note = "Dinner",
            date = LocalDate.of(2024, 1, 20),
            createdAt = Instant.now()
        ),
        Expense(
            id = "4",
            amount = 75.0,
            currency = "LKR",
            category = Category.ENTERTAINMENT,
            note = "Movie",
            date = LocalDate.of(2024, 1, 25),
            createdAt = Instant.now()
        )
    )

    @Before
    fun setup() {
        repository = mockk()
        every { repository.getExpenses() } returns flowOf(testExpenses)
        getExpensesUseCase = GetExpensesUseCase(repository)
        filterExpensesUseCase = FilterExpensesUseCase(getExpensesUseCase)
    }

    @Test
    fun `filter by category returns only matching expenses`() = runTest {
        // When
        val result = filterExpensesUseCase(category = Category.FOOD).first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.category == Category.FOOD })
        assertEquals("1", result[0].id)
        assertEquals("3", result[1].id)
    }

    @Test
    fun `filter by date range returns expenses within range`() = runTest {
        // Given
        val startDate = LocalDate.of(2024, 1, 12)
        val endDate = LocalDate.of(2024, 1, 22)

        // When
        val result = filterExpensesUseCase(
            startDate = startDate,
            endDate = endDate
        ).first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { !it.date.isBefore(startDate) && !it.date.isAfter(endDate) })
        assertEquals("2", result[0].id) // Jan 15
        assertEquals("3", result[1].id) // Jan 20
    }

    @Test
    fun `empty filter returns all expenses`() = runTest {
        // When
        val result = filterExpensesUseCase(
            category = null,
            startDate = null,
            endDate = null
        ).first()

        // Then
        assertEquals(4, result.size)
        assertEquals(testExpenses, result)
    }

    @Test
    fun `filter by category and date range uses AND logic`() = runTest {
        val startDate = LocalDate.of(2024, 1, 20)
        val endDate = LocalDate.of(2024, 1, 30)

        val result = filterExpensesUseCase(
            category = Category.FOOD,
            startDate = startDate,
            endDate = endDate
        ).first()

        assertEquals(1, result.size)
        assertEquals("3", result[0].id)
        assertEquals(Category.FOOD, result[0].category)
    }

    @Test
    fun `filter with no matching expenses returns empty list`() = runTest {
        // Given
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 12, 31)

        // When
        val result = filterExpensesUseCase(
            startDate = startDate,
            endDate = endDate
        ).first()

        // Then
        assertTrue(result.isEmpty())
    }
}
