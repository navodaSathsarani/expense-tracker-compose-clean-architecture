package com.example.expensetracker.presentation.expense_list

import androidx.annotation.StringRes
import com.example.expensetracker.R
import com.example.expensetracker.domain.model.Expense

sealed interface ExpenseListUiState {
    data object Loading : ExpenseListUiState
    data class Empty(
        @StringRes val messageRes: Int = R.string.empty_expenses_message,
        @StringRes val actionTextRes: Int = R.string.add_expense_action
    ) : ExpenseListUiState
    data class Success(val expenses: List<Expense>) : ExpenseListUiState
    data class Error(val message: String) : ExpenseListUiState
}
