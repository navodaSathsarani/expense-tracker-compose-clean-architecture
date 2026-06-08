package com.example.expensetracker.presentation.expense_list

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.expensetracker.presentation.filter.ExpenseFilter
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val filterExpensesUseCase: FilterExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val refreshExpensesUseCase: RefreshExpensesUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val FILTER_CATEGORY_KEY = "filter_category"
        const val FILTER_START_DATE_KEY = "filter_start_date"
        const val FILTER_END_DATE_KEY = "filter_end_date"
    }

    private val _uiState = MutableStateFlow<ExpenseListUiState>(ExpenseListUiState.Loading)
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    private val filterCategoryFlow = MutableStateFlow(
        savedStateHandle.get<String>(FILTER_CATEGORY_KEY)?.let { name ->
            runCatching { Category.valueOf(name) }.getOrNull()
        }
    )
    @RequiresApi(Build.VERSION_CODES.O)
    private val filterStartDateFlow = MutableStateFlow(
        savedStateHandle.get<String>(FILTER_START_DATE_KEY)?.let { iso ->
            runCatching { LocalDate.parse(iso) }.getOrNull()
        }
    )
    @RequiresApi(Build.VERSION_CODES.O)
    private val filterEndDateFlow = MutableStateFlow(
        savedStateHandle.get<String>(FILTER_END_DATE_KEY)?.let { iso ->
            runCatching { LocalDate.parse(iso) }.getOrNull()
        }
    )

    init {
        loadExpenses()
        syncExpensesInBackground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentFilter(): ExpenseFilter = ExpenseFilter(
        category = filterCategoryFlow.value,
        startDate = filterStartDateFlow.value,
        endDate = filterEndDateFlow.value
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyFilter(filter: ExpenseFilter) {
        filterCategoryFlow.value = filter.category
        filterStartDateFlow.value = filter.startDate
        filterEndDateFlow.value = filter.endDate

        if (filter.category != null) {
            savedStateHandle[FILTER_CATEGORY_KEY] = filter.category.name
        } else {
            savedStateHandle.remove<String>(FILTER_CATEGORY_KEY)
        }
        if (filter.startDate != null) {
            savedStateHandle[FILTER_START_DATE_KEY] = filter.startDate.toString()
        } else {
            savedStateHandle.remove<String>(FILTER_START_DATE_KEY)
        }
        if (filter.endDate != null) {
            savedStateHandle[FILTER_END_DATE_KEY] = filter.endDate.toString()
        } else {
            savedStateHandle.remove<String>(FILTER_END_DATE_KEY)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadExpenses() {
        viewModelScope.launch {
            combine(
                filterCategoryFlow,
                filterStartDateFlow,
                filterEndDateFlow
            ) { category, startDate, endDate ->
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
                        e.message ?: "Failed"
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

    private fun syncExpensesInBackground() {
        viewModelScope.launch {
            try {
                refreshExpensesUseCase()
            } catch (_: Exception) {
                // Keep showing cached Room data when background sync fails.
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
                    e.message ?: "Failed"
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
                    e.message ?: "Failed"
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
