package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.CategorySummary
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<CategorySummary>> = repository.getSummary()
}
