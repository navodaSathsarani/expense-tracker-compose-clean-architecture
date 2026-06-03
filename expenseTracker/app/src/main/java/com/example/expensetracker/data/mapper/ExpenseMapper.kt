package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.remote.dto.ExpenseDto
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import java.time.Instant
import java.time.LocalDate

fun ExpenseDto.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        currency = currency,
        category = Category.valueOf(category),
        note = note,
        date = LocalDate.parse(date),
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun ExpenseDto.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        currency = currency,
        category = category,
        note = note,
        date = LocalDate.parse(date),
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        currency = currency,
        category = Category.valueOf(category),
        note = note,
        date = date,
        createdAt = createdAt
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        currency = currency,
        category = category.name,
        note = note,
        date = date,
        createdAt = createdAt
    )
}

fun Expense.toDto(): ExpenseDto {
    return ExpenseDto(
        id = id,
        amount = amount,
        currency = currency,
        category = category.name,
        note = note,
        date = date.toString(),
        createdAt = createdAt.toEpochMilli()
    )
}
