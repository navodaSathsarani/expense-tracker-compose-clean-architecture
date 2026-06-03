package com.example.expensetracker.data.remote.datasource

import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.data.remote.dto.ExpenseDto
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class MockExpenseDataSource @Inject constructor() : ExpenseApi {

    private val mockExpenses = mutableListOf(
        ExpenseDto(
            id = "1",
            amount = 1250.50,
            currency = "LKR",
            category = "FOOD",
            note = "Lunch at restaurant",
            date = LocalDate.now().minusDays(1).toString(),
            createdAt = Instant.now().minusSeconds(86400).toEpochMilli()
        ),
        ExpenseDto(
            id = "2",
            amount = 500.00,
            currency = "LKR",
            category = "TRANSPORT",
            note = "Uber ride home",
            date = LocalDate.now().minusDays(2).toString(),
            createdAt = Instant.now().minusSeconds(172800).toEpochMilli()
        ),
        ExpenseDto(
            id = "3",
            amount = 2500.00,
            currency = "LKR",
            category = "ENTERTAINMENT",
            note = "Movie tickets",
            date = LocalDate.now().minusDays(3).toString(),
            createdAt = Instant.now().minusSeconds(259200).toEpochMilli()
        ),
        ExpenseDto(
            id = "4",
            amount = 15000.00,
            currency = "LKR",
            category = "SHOPPING",
            note = "New clothes",
            date = LocalDate.now().minusDays(5).toString(),
            createdAt = Instant.now().minusSeconds(432000).toEpochMilli()
        ),
        ExpenseDto(
            id = "5",
            amount = 8500.00,
            currency = "LKR",
            category = "BILLS",
            note = "Electricity bill",
            date = LocalDate.now().minusDays(7).toString(),
            createdAt = Instant.now().minusSeconds(604800).toEpochMilli()
        ),
        ExpenseDto(
            id = "6",
            amount = 750.00,
            currency = "LKR",
            category = "FOOD",
            note = "Groceries",
            date = LocalDate.now().minusDays(8).toString(),
            createdAt = Instant.now().minusSeconds(691200).toEpochMilli()
        ),
        ExpenseDto(
            id = "7",
            amount = 300.00,
            currency = "LKR",
            category = "TRANSPORT",
            note = "Bus fare",
            date = LocalDate.now().minusDays(9).toString(),
            createdAt = Instant.now().minusSeconds(777600).toEpochMilli()
        ),
        ExpenseDto(
            id = "8",
            amount = 1200.00,
            currency = "LKR",
            category = "OTHER",
            note = "Pharmacy",
            date = LocalDate.now().minusDays(10).toString(),
            createdAt = Instant.now().minusSeconds(864000).toEpochMilli()
        ),
        ExpenseDto(
            id = "9",
            amount = 5000.00,
            currency = "LKR",
            category = "ENTERTAINMENT",
            note = "Concert tickets",
            date = LocalDate.now().minusDays(12).toString(),
            createdAt = Instant.now().minusSeconds(1036800).toEpochMilli()
        ),
        ExpenseDto(
            id = "10",
            amount = 12000.00,
            currency = "LKR",
            category = "BILLS",
            note = "Internet bill",
            date = LocalDate.now().minusDays(15).toString(),
            createdAt = Instant.now().minusSeconds(1296000).toEpochMilli()
        )
    )

    override suspend fun getExpenses(): List<ExpenseDto> {
        delay(1000) // Simulate network delay
        return mockExpenses.toList()
    }

    override suspend fun addExpense(expense: ExpenseDto) {
        delay(1000) // Simulate network delay
        mockExpenses.add(expense)
    }

    override suspend fun deleteExpense(id: String) {
        delay(1000) // Simulate network delay
        mockExpenses.removeIf { it.id == id }
    }
}
