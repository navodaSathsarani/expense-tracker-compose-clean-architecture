package com.example.expensetracker.presentation.expense_list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.expensetracker.domain.model.Expense
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.presentation.components.EmptyState
import com.example.expensetracker.presentation.components.ErrorState
import com.example.expensetracker.presentation.components.ExpenseItem
import com.example.expensetracker.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToSummary: () -> Unit,
    onNavigateToFilter: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expensePendingDelete by remember { mutableStateOf<Expense?>(null) }

    expensePendingDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expensePendingDelete = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_confirm_message,
                        expense.currency,
                        expense.amount
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(expense.id)
                        expensePendingDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { expensePendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.expense_list_title)) },
                actions = {
                    IconButton(onClick = onNavigateToFilter) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.cd_filter_expenses)
                        )
                    }
                    IconButton(onClick = onNavigateToSummary) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = stringResource(R.string.cd_view_summary)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExpense) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_expense)
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
                    message = stringResource(state.messageRes),
                    actionText = stringResource(state.actionTextRes),
                    onAction = if (state.actionTextRes == R.string.change_filter_action) {
                        onNavigateToFilter
                    } else {
                        onNavigateToAddExpense
                    },
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
                            onDelete = { expensePendingDelete = expense }
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
