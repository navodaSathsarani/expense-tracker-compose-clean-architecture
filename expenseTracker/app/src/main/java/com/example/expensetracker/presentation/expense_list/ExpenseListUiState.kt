package com.example.expensetracker.presentation.expense_list

import com.example.expensetracker.domain.model.Expense

sealed interface ExpenseListUiState {
    data object Loading : ExpenseListUiState
    data object Empty : ExpenseListUiState
    data class Success(val expenses: List<Expense>) : ExpenseListUiState
    data class Error(val message: String) : ExpenseListUiState
}
