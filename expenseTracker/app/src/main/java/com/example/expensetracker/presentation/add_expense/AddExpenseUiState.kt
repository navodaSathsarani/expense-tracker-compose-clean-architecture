package com.example.expensetracker.presentation.add_expense

import com.example.expensetracker.domain.model.Category

data class AddExpenseUiState(
    val amount: String = "",
    val category: Category = Category.FOOD,
    val note: String = "",
    val amountError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
