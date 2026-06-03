package com.example.expensetracker.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.domain.model.Category

@Composable
fun CategoryChip(
    category: Category,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = modifier.padding(horizontal = 4.dp)
    )
}
