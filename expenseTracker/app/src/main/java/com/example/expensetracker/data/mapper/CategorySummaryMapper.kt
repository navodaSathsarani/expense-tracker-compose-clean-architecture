package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.remote.dto.CategorySummaryDto
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.CategorySummary

fun CategorySummaryDto.toDomain(): CategorySummary {
    return CategorySummary(
        category = Category.valueOf(category),
        total = total,
        count = count,
        percentage = percentage
    )
}

fun CategorySummary.toDto(): CategorySummaryDto {
    return CategorySummaryDto(
        category = category.name,
        total = total,
        count = count,
        percentage = percentage
    )
}
