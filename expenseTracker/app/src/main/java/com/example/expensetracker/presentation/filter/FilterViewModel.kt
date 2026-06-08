package com.example.expensetracker.presentation.filter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.expensetracker.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate.asStateFlow()

    fun setInitialFilter(filter: ExpenseFilter) {
        _selectedCategory.value = filter.category
        _startDate.value = filter.startDate
        _endDate.value = filter.endDate
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartDate(date: LocalDate?) {
        _startDate.value = date
        val end = _endDate.value
        if (date != null && end != null && end.isBefore(date)) {
            _endDate.value = date
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setEndDate(date: LocalDate?) {
        _endDate.value = date
        val start = _startDate.value
        if (date != null && start != null && start.isAfter(date)) {
            _startDate.value = date
        }
    }

    fun clearSelection() {
        _selectedCategory.value = null
        _startDate.value = null
        _endDate.value = null
    }

    fun currentFilter(): ExpenseFilter = ExpenseFilter(
        category = _selectedCategory.value,
        startDate = _startDate.value,
        endDate = _endDate.value
    )
}
