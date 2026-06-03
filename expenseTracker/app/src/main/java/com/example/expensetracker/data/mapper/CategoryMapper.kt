package com.example.expensetracker.data.mapper

import com.example.expensetracker.domain.model.Category

/** API contract uses lowercase category slugs (e.g. `"food"`). */
fun String.toDomainCategory(): Category = Category.valueOf(this.uppercase())

fun Category.toApiCategory(): String = name.lowercase()
