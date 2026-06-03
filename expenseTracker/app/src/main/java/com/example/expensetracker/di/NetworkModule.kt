package com.example.expensetracker.di

import com.example.expensetracker.data.remote.api.ExpenseApi
import com.example.expensetracker.data.remote.datasource.MockExpenseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideExpenseApi(
        mockExpenseDataSource: MockExpenseDataSource
    ): ExpenseApi {
        return mockExpenseDataSource
    }
}
