package com.example.expensetracker.data.remote.dto

data class ExpenseDto(
    val id: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val note: String?,
    val date: String,
    val createdAt: Long
)
