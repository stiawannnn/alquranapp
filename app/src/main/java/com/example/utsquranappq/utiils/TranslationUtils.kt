package com.example.utsquranappq.utiils

// Terjemahan nama surah (dari English ke Indonesia)
val surahTranslations = mapOf(
    "Al-Faatiha" to "Al-Fatihah",
    "Al-Baqara" to "Al-Baqarah",
    "Aal-i-Imraan" to "Ali 'Imran",
    "An-Nisa" to "An-Nisa'",
    "Al-Maeda" to "Al-Ma'idah"
    // Tambahkan surah lainnya...
)

// Terjemahan arti nama surah (dari English ke Indonesia)
val surahMeaningTranslations = mapOf(
    "The Opening" to "Pembukaan",
    "The Cow" to "Sapi Betina",
    "The Family of Imran" to "Keluarga Imran",
    "The Women" to "Wanita",
    "The Table Spread" to "Hidangan"
    // Tambahkan yang lain...
)

// Terjemahan jenis wahyu (Meccan → Makkiyah, Medinan → Madaniyah)
val revelationTranslations = mapOf(
    "Meccan" to "Makkiyah",
    "Medinan" to "Madaniyah"
)
fun getTranslation(englishName: String, englishTranslation: String, revelationType: String): Triple<String, String, String> {
    val surahIndo = surahTranslations[englishName] ?: englishName
    val meaningIndo = surahMeaningTranslations[englishTranslation] ?: englishTranslation
    val revelationIndo = revelationTranslations[revelationType] ?: revelationType

    return Triple(surahIndo, meaningIndo, revelationIndo)
}


