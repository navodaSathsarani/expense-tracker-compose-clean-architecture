package com.example.expensetracker.data.remote.datasource

import com.example.expensetracker.data.mapper.toApiCategory
import com.example.expensetracker.data.mapper.toDomain
import com.example.expensetracker.data.mapper.toDto
import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.data.remote.dto.CategorySummaryDto
import com.example.expensetracker.data.remote.dto.ExpenseDto
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.util.CategorySummaryCalculator
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class MockExpenseDataSource @Inject constructor() : ExpenseApi {

    private val mockExpenses = mutableListOf(
        expenseDto("1", 1250.50, Category.FOOD, "Lunch at restaurant", 1),
        expenseDto("2", 500.00, Category.TRANSPORT, "Uber ride home", 2),
        expenseDto("3", 2500.00, Category.ENTERTAINMENT, "Movie tickets", 3),
        expenseDto("4", 15000.00, Category.SHOPPING, "New clothes", 5),
        expenseDto("5", 8500.00, Category.BILLS, "Electricity bill", 7),
        expenseDto("6", 750.00, Category.FOOD, "Groceries", 8),
        expenseDto("7", 300.00, Category.TRANSPORT, "Bus fare", 9),
        expenseDto("8", 1200.00, Category.OTHER, "Pharmacy", 10),
        expenseDto("9", 5000.00, Category.ENTERTAINMENT, "Concert tickets", 12),
        expenseDto("10", 12000.00, Category.BILLS, "Internet bill", 15)
    )

    override suspend fun getExpenses(
        category: String?,
        from: String?,
        to: String?
    ): List<ExpenseDto> {
        delay(1000)
        var result = mockExpenses.toList()

        if (category != null) {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        val startDate = from?.let { LocalDate.parse(it) }
        val endDate = to?.let { LocalDate.parse(it) }

        if (startDate != null || endDate != null) {
            result = result.filter { dto ->
                val date = LocalDate.parse(dto.date)
                (startDate == null || !date.isBefore(startDate)) &&
                    (endDate == null || !date.isAfter(endDate))
            }
        }

        return result
    }

    override suspend fun addExpense(expense: ExpenseDto) {
        delay(1000)
        mockExpenses.add(expense)
    }

    override suspend fun deleteExpense(id: String) {
        delay(1000)
        mockExpenses.removeIf { it.id == id }
    }

    override suspend fun getSummary(): List<CategorySummaryDto> {
        delay(1000)
        return CategorySummaryCalculator
            .fromExpenses(mockExpenses.map { it.toDomain() })
            .map { it.toDto() }
    }

    private fun expenseDto(
        id: String,
        amount: Double,
        category: Category,
        note: String,
        daysAgo: Long
    ): ExpenseDto {
        val date = LocalDate.now().minusDays(daysAgo)
        return ExpenseDto(
            id = id,
            amount = amount,
            currency = "LKR",
            category = category.toApiCategory(),
            note = note,
            date = date.toString(),
            createdAt = Instant.now().minusSeconds(daysAgo * 86400).toEpochMilli()
        )
    }
}
