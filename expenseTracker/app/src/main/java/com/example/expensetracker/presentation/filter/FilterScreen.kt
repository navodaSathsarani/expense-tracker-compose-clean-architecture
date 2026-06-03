package com.example.expensetracker.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.presentation.util.displayName
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    onNavigateBack: () -> Unit,
    onApplyFilter: (ExpenseFilter) -> Unit,
    initialFilter: ExpenseFilter = ExpenseFilter(),
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    LaunchedEffect(initialFilter) {
        viewModel.setInitialFilter(initialFilter)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filter_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_by_category),
                style = MaterialTheme.typography.titleMedium
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category.displayName()) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.filter_by_date_range),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = { showStartPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = startDate?.format(dateFormatter)
                        ?: stringResource(R.string.filter_select_start)
                )
            }

            OutlinedButton(
                onClick = { showEndPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = endDate?.format(dateFormatter)
                        ?: stringResource(R.string.filter_select_end)
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.setStartDate(null)
                    viewModel.setEndDate(null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = startDate != null || endDate != null
            ) {
                Text(stringResource(R.string.filter_clear_dates))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onApplyFilter(viewModel.currentFilter()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.apply_filter))
                }

                OutlinedButton(
                    onClick = {
                        viewModel.clearSelection()
                        onApplyFilter(ExpenseFilter())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.clear_filter))
                }
            }
        }
    }

    if (showStartPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { millis ->
                            viewModel.setStartDate(millis.toLocalDate())
                        }
                        showStartPicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { millis ->
                            viewModel.setEndDate(millis.toLocalDate())
                        }
                        showEndPicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

private fun LocalDate.toEpochMilli(): Long =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
