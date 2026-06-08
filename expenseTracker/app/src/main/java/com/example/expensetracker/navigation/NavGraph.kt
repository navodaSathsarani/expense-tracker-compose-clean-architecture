package com.example.expensetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.add_expense.AddExpenseScreen
import com.example.expensetracker.presentation.expense_list.ExpenseListScreen
import com.example.expensetracker.presentation.expense_list.ExpenseListViewModel
import com.example.expensetracker.presentation.filter.FilterScreen
import com.example.expensetracker.presentation.summary.SummaryScreen

sealed class Screen(val route: String) {
    data object ExpenseList : Screen("expense_list")
    data object AddExpense : Screen("add_expense")
    data object Summary : Screen("summary")
    data object Filter : Screen("filter")
}

@Composable
fun ExpenseTrackerNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.ExpenseList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.ExpenseList.route) {
            ExpenseListScreen(
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToSummary = {
                    navController.navigate(Screen.Summary.route)
                },
                onNavigateToFilter = {
                    navController.navigate(Screen.Filter.route)
                }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Summary.route) {
            SummaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Filter.route) {
            val listEntry = navController.getBackStackEntry(Screen.ExpenseList.route)
            val listViewModel: ExpenseListViewModel = hiltViewModel(listEntry)

            FilterScreen(
                initialFilter = listViewModel.currentFilter(),
                onNavigateBack = {
                    navController.popBackStack()
                },
                onApplyFilter = { filter ->
                    listViewModel.applyFilter(filter)
                    navController.popBackStack()
                }
            )
        }
    }
}
