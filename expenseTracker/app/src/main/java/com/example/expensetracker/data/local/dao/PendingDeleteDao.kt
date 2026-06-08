package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.data.local.entity.PendingDeleteEntity

@Dao
interface PendingDeleteDao {
    @Query("SELECT id FROM pending_deletes")
    suspend fun getAllIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingDelete: PendingDeleteEntity)

    @Query("DELETE FROM pending_deletes WHERE id = :id")
    suspend fun delete(id: String)
}
