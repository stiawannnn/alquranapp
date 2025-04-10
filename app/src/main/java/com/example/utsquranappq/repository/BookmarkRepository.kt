package com.example.utsquranappq.repository

import android.content.Context
import com.example.utsquranappq.data.QuranDatabase
import com.example.utsquranappq.model.Bookmark
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(context: Context) {
    private val bookmarkDao = QuranDatabase.getDatabase(context).bookmarkDao()

    fun getAllBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()

    suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun removeBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark)
    }

    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return bookmarkDao.getBookmarkByAyah(surahNumber, ayahNumber) != null
    }

    suspend fun getBookmark(surahNumber: Int, ayahNumber: Int): Bookmark? {
        return bookmarkDao.getBookmarkByAyah(surahNumber, ayahNumber)
    }
}