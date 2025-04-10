package com.example.utsquranappq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val juzNumber: Int? = null, // Opsional untuk konteks Juz
    val timestamp: Long = System.currentTimeMillis()
)