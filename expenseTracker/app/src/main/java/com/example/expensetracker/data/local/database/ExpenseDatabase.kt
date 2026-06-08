package com.example.expensetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.dao.PendingDeleteDao
import com.example.expensetracker.data.local.entity.Converters
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.local.entity.PendingDeleteEntity

@Database(
    entities = [ExpenseEntity::class, PendingDeleteEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun pendingDeleteDao(): PendingDeleteDao
}
