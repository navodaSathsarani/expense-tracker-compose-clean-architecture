package com.example.expensetracker.di

import com.example.expensetracker.data.repository.ExpenseRepositoryImpl
import com.example.expensetracker.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}
