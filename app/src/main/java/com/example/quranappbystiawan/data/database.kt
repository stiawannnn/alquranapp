package com.example.quranappbystiawan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quranappbystiawan.model.Bookmark

@Database(entities = [Bookmark::class], version = 2, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: QuranDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE bookmarks ADD COLUMN surahName TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database"
                )
                    .addMigrations(MIGRATION_1_2) // Tambah migrasi
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}