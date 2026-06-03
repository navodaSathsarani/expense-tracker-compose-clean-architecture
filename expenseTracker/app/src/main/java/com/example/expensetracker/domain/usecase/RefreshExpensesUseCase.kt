package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class RefreshExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke() {
        repository.refreshExpenses()
    }
}
