package app.dev.quranappbystiawan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.dev.quranappbystiawan.model.Bookmark
import app.dev.quranappbystiawan.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(context: Context) : ViewModel() {
    private val repository = BookmarkRepository(context)

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllBookmarks().collect { bookmarkList ->
                _bookmarks.value = bookmarkList
            }
        }
    }

    fun addBookmark(surahNumber: Int, ayahNumber: Int, surahName: String, juzNumber: Int? = null) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                juzNumber = juzNumber
            )
            if (!repository.isBookmarked(surahNumber, ayahNumber)) {
                repository.addBookmark(bookmark)
            }
        }
    }

    fun removeBookmark(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch {
            repository.getBookmark(surahNumber, ayahNumber)?.let { bookmark ->
                repository.removeBookmark(bookmark)
            }
        }
    }

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return _bookmarks.value.any { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
    }
}