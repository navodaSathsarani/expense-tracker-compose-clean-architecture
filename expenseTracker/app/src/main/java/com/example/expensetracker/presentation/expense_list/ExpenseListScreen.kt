package com.example.expensetracker.presentation.expense_list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.presentation.components.EmptyState
import com.example.expensetracker.presentation.components.ErrorState
import com.example.expensetracker.presentation.components.ExpenseItem
import com.example.expensetracker.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToSummary: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker") },
                actions = {
                    IconButton(onClick = onNavigateToSummary) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "View Summary"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExpense) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ExpenseListUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is ExpenseListUiState.Empty -> {
                EmptyState(
                    message = "No expenses yet.\nStart tracking your spending!",
                    actionText = "Add Expense",
                    onAction = onNavigateToAddExpense,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is ExpenseListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(
                        items = state.expenses,
                        key = { it.id }
                    ) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = { viewModel.deleteExpense(expense.id) }
                        )
                    }
                }
            }

            is ExpenseListUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.refreshExpenses() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
