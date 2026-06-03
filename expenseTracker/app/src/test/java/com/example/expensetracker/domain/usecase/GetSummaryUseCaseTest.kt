package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.util.CategorySummaryCalculator
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
import kotlin.math.abs

class GetSummaryUseCaseTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var getSummaryUseCase: GetSummaryUseCase

    @Before
    fun setup() {
        repository = mockk()
        getSummaryUseCase = GetSummaryUseCase(repository)
    }

    private fun mockSummaryFor(expenses: List<Expense>) {
        every { repository.getSummary() } returns flowOf(
            CategorySummaryCalculator.fromExpenses(expenses)
        )
    }

    @Test
    fun `summary percentages sum to 100`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 100.0, category = Category.FOOD),
            createExpense(id = "2", amount = 50.0, category = Category.TRANSPORT),
            createExpense(id = "3", amount = 150.0, category = Category.SHOPPING)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        val totalPercentage = summary.sumOf { it.percentage }
        assertTrue(abs(totalPercentage - 100.0) < 0.01)
    }

    @Test
    fun `empty expense list returns empty summary`() = runTest {
        mockSummaryFor(emptyList())

        val summary = getSummaryUseCase().first()

        assertTrue(summary.isEmpty())
    }

    @Test
    fun `summary calculates correct totals per category`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 100.0, category = Category.FOOD),
            createExpense(id = "2", amount = 150.0, category = Category.FOOD),
            createExpense(id = "3", amount = 50.0, category = Category.TRANSPORT)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        val foodSummary = summary.find { it.category == Category.FOOD }
        val transportSummary = summary.find { it.category == Category.TRANSPORT }

        assertEquals(250.0, foodSummary?.total ?: 0.0, 0.01)
        assertEquals(50.0, transportSummary?.total ?: 0.0, 0.01)
    }

    @Test
    fun `summary calculates correct count per category`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 100.0, category = Category.FOOD),
            createExpense(id = "2", amount = 150.0, category = Category.FOOD),
            createExpense(id = "3", amount = 75.0, category = Category.FOOD),
            createExpense(id = "4", amount = 50.0, category = Category.TRANSPORT)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        val foodSummary = summary.find { it.category == Category.FOOD }
        val transportSummary = summary.find { it.category == Category.TRANSPORT }

        assertEquals(3, foodSummary?.count)
        assertEquals(1, transportSummary?.count)
    }

    @Test
    fun `summary calculates correct percentages`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 300.0, category = Category.FOOD),
            createExpense(id = "2", amount = 100.0, category = Category.TRANSPORT)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        val foodSummary = summary.find { it.category == Category.FOOD }
        val transportSummary = summary.find { it.category == Category.TRANSPORT }

        assertEquals(75.0, foodSummary?.percentage ?: 0.0, 0.01)
        assertEquals(25.0, transportSummary?.percentage ?: 0.0, 0.01)
    }

    @Test
    fun `summary is sorted by total in descending order`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 50.0, category = Category.FOOD),
            createExpense(id = "2", amount = 200.0, category = Category.SHOPPING),
            createExpense(id = "3", amount = 100.0, category = Category.TRANSPORT)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        assertEquals(Category.SHOPPING, summary[0].category)
        assertEquals(Category.TRANSPORT, summary[1].category)
        assertEquals(Category.FOOD, summary[2].category)
    }

    @Test
    fun `summary excludes categories with no expenses`() = runTest {
        val expenses = listOf(
            createExpense(id = "1", amount = 100.0, category = Category.FOOD)
        )
        mockSummaryFor(expenses)

        val summary = getSummaryUseCase().first()

        assertEquals(1, summary.size)
        assertEquals(Category.FOOD, summary[0].category)
    }

    private fun createExpense(
        id: String,
        amount: Double,
        category: Category
    ) = Expense(
        id = id,
        amount = amount,
        currency = "LKR",
        category = category,
        note = "Test",
        date = LocalDate.now(),
        createdAt = Instant.now()
    )
}
