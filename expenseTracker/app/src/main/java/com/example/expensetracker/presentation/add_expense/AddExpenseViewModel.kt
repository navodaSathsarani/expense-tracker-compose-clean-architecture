package com.example.expensetracker.presentation.add_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount, amountError = null) }
    }

    fun onCategoryChange(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveExpense() {
        val currentState = _uiState.value

        // Validate amount
        val amountValue = currentState.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.update { it.copy(amountError = "Please enter a valid amount greater than 0") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val expense = Expense(
                    id = UUID.randomUUID().toString(),
                    amount = amountValue,
                    currency = "LKR",
                    category = currentState.category,
                    note = currentState.note.takeIf { it.isNotBlank() },
                    date = LocalDate.now(),
                    createdAt = Instant.now()
                )

                addExpenseUseCase(expense)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        amountError = e.message ?: "Failed to save expense"
                    )
                }
            }
        }
    }
}
