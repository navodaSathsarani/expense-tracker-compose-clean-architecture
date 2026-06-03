package com.example.expensetracker.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.expensetracker.R
import com.example.expensetracker.domain.model.Category

@StringRes
fun Category.labelRes(): Int = when (this) {
    Category.FOOD -> R.string.category_food
    Category.TRANSPORT -> R.string.category_transport
    Category.ENTERTAINMENT -> R.string.category_entertainment
    Category.SHOPPING -> R.string.category_shopping
    Category.BILLS -> R.string.category_bills
    Category.OTHER -> R.string.category_other
}

@Composable
fun Category.displayName(): String = stringResource(labelRes())
