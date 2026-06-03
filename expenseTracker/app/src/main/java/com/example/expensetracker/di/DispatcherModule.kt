package com.example.expensetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}
