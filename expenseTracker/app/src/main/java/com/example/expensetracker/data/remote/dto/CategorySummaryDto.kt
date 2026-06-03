package com.example.expensetracker.data.remote.dto

data class CategorySummaryDto(
    val category: String,
    val total: Double,
    val count: Int,
    val percentage: Double
)
