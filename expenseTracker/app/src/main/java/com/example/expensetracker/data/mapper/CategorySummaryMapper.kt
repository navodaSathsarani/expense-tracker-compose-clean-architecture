package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.remote.dto.CategorySummaryDto
import com.example.expensetracker.domain.model.CategorySummary

fun CategorySummaryDto.toDomain(): CategorySummary {
    return CategorySummary(
        category = category.toDomainCategory(),
        total = total,
        count = count,
        percentage = percentage
    )
}

fun CategorySummary.toDto(): CategorySummaryDto {
    return CategorySummaryDto(
        category = category.toApiCategory(),
        total = total,
        count = count,
        percentage = percentage
    )
}
