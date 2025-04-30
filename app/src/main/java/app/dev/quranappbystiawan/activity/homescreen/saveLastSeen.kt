package app.dev.quranappbystiawan.activity.homescreen

import android.content.Context

fun saveLastSeen(context: Context, surah: Int? = null, ayah: Int? = null, juz: Int? = null, juzSurah: Int? = null, juzAyah: Int? = null) {
    val prefs = context.getSharedPreferences("QuranPrefs", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        if (surah != null && ayah != null) {
            putInt("lastSurah", surah)
            putInt("lastAyah", ayah)
            putInt("lastJuz", -1)
            putInt("lastJuzSurah", -1)
            putInt("lastJuzAyah", -1)
        } else if (juz != null && juzSurah != null && juzAyah != null) {
            putInt("lastJuz", juz)
            putInt("lastJuzSurah", juzSurah)
            putInt("lastJuzAyah", juzAyah)
            putInt("lastSurah", -1)
            putInt("lastAyah", -1)
        }
        apply()
    }
}
