package com.example.expensetracker.presentation.expense_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpenseListUiState>(ExpenseListUiState.Loading)
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
        refreshExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            getExpensesUseCase()
                .catch { e ->
                    _uiState.value = ExpenseListUiState.Error(
                        e.message ?: "Failed to load expenses"
                    )
                }
                .collect { expenses ->
                    _uiState.value = if (expenses.isEmpty()) {
                        ExpenseListUiState.Empty
                    } else {
                        ExpenseListUiState.Success(expenses)
                    }
                }
        }
    }

    fun refreshExpenses() {
        viewModelScope.launch {
            try {
                _uiState.value = ExpenseListUiState.Loading
                repository.refreshExpenses()
            } catch (e: Exception) {
                _uiState.value = ExpenseListUiState.Error(
                    e.message ?: "Failed to refresh expenses"
                )
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            try {
                deleteExpenseUseCase(id)
            } catch (e: Exception) {
                _uiState.value = ExpenseListUiState.Error(
                    e.message ?: "Failed to delete expense"
                )
            }
        }
    }
}
