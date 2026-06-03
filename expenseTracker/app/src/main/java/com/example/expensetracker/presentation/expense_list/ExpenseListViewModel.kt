package com.example.expensetracker.presentation.expense_list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.example.expensetracker.domain.usecase.FilterExpensesUseCase
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.RefreshExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val filterExpensesUseCase: FilterExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val refreshExpensesUseCase: RefreshExpensesUseCase,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val FILTER_CATEGORY_KEY = "filter_category"
        const val FILTER_START_DATE_KEY = "filter_start_date"
        const val FILTER_END_DATE_KEY = "filter_end_date"
    }

    private val _uiState = MutableStateFlow<ExpenseListUiState>(ExpenseListUiState.Loading)
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    private val filterCategoryFlow = savedStateHandle.getStateFlow<String?>(FILTER_CATEGORY_KEY, null)
    private val filterStartDateFlow = savedStateHandle.getStateFlow<String?>(FILTER_START_DATE_KEY, null)
    private val filterEndDateFlow = savedStateHandle.getStateFlow<String?>(FILTER_END_DATE_KEY, null)

    init {
        loadExpenses()
        refreshExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            combine(
                filterCategoryFlow,
                filterStartDateFlow,
                filterEndDateFlow
            ) { categoryName, startIso, endIso ->
                val category = categoryName?.let { name ->
                    runCatching { Category.valueOf(name) }.getOrNull()
                }
                val startDate = startIso?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                val endDate = endIso?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                Triple(category, startDate, endDate)
            }
                .flatMapLatest { (category, startDate, endDate) ->
                    val hasFilter = category != null || startDate != null || endDate != null
                    val flow = if (hasFilter) {
                        filterExpensesUseCase(
                            category = category,
                            startDate = startDate,
                            endDate = endDate
                        )
                    } else {
                        getExpensesUseCase()
                    }
                    flow.map { expenses ->
                        FilterState(category, startDate, endDate, hasFilter, expenses)
                    }
                }
                .catch { e ->
                    _uiState.value = ExpenseListUiState.Error(
                        e.message ?: context.getString(R.string.error_load_expenses)
                    )
                }
                .collect { state ->
                    _uiState.value = when {
                        state.expenses.isEmpty() && state.hasFilter -> ExpenseListUiState.Empty(
                            messageRes = R.string.empty_filtered_message,
                            actionTextRes = R.string.change_filter_action
                        )
                        state.expenses.isEmpty() -> ExpenseListUiState.Empty()
                        else -> ExpenseListUiState.Success(state.expenses)
                    }
                }
        }
    }

    fun refreshExpenses() {
        viewModelScope.launch {
            try {
                _uiState.value = ExpenseListUiState.Loading
                refreshExpensesUseCase()
            } catch (e: Exception) {
                _uiState.value = ExpenseListUiState.Error(
                    e.message ?: context.getString(R.string.error_refresh_expenses)
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
                    e.message ?: context.getString(R.string.error_delete_expense)
                )
            }
        }
    }

    private data class FilterState(
        val category: Category?,
        val startDate: LocalDate?,
        val endDate: LocalDate?,
        val hasFilter: Boolean,
        val expenses: List<Expense>
    )
}
