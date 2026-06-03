package com.example.expensetracker.domain.model

data class CategorySummary(
    val category: Category,
    val total: Double,
    val count: Int,
    val percentage: Double
)
