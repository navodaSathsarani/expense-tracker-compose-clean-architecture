package com.example.expensetracker.presentation.add_expense

import androidx.annotation.StringRes
import com.example.expensetracker.domain.model.Category

data class AddExpenseUiState(
    val amount: String = "",
    val category: Category = Category.FOOD,
    val note: String = "",
    @StringRes val amountErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
