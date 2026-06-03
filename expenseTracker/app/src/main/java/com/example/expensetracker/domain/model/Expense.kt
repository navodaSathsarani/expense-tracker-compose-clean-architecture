package com.example.expensetracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class Expense(
    val id: String,
    val amount: Double,
    val currency: String,
    val category: Category,
    val note: String?,
    val date: LocalDate,
    val createdAt: Instant
)
