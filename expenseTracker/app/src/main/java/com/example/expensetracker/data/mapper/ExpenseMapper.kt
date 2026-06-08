package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.local.entity.SyncStatus
import com.example.expensetracker.data.remote.dto.ExpenseDto
import com.example.expensetracker.domain.model.Expense
import java.time.Instant
import java.time.LocalDate

fun ExpenseDto.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        currency = currency,
        category = category.toDomainCategory(),
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
        category = category.toDomainCategory().name,
        note = note,
        date = LocalDate.parse(date),
        createdAt = Instant.ofEpochMilli(createdAt),
        syncStatus = SyncStatus.SYNCED
    )
}

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        currency = currency,
        category = category.toDomainCategory(),
        note = note,
        date = date,
        createdAt = createdAt
    )
}

fun Expense.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        currency = currency,
        category = category.name,
        note = note,
        date = date,
        createdAt = createdAt,
        syncStatus = syncStatus
    )
}

fun Expense.toDto(): ExpenseDto {
    return ExpenseDto(
        id = id,
        amount = amount,
        currency = currency,
        category = category.toApiCategory(),
        note = note,
        date = date.toString(),
        createdAt = createdAt.toEpochMilli()
    )
}
