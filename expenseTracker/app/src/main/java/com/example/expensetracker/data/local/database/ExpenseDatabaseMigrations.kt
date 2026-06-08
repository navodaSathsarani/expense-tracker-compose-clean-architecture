package com.example.expensetracker.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE expenses ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pending_deletes (
                id TEXT NOT NULL PRIMARY KEY
            )
            """.trimIndent()
        )
    }
}
