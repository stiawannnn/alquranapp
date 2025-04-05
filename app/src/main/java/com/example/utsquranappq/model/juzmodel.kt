package com.example.utsquranappq.model
data class JuzInfo(
    val number: Int,
    val startSurah: String,
    val startAyah: Int
)
data class JuzResponse(
    val code: Int,
    val status: String,
    val data: JuzData
)

data class JuzData(
    val number: Int,
    val ayahs: List<AyahEdition>
)
val juzListStatic = listOf(
    JuzInfo(1, "Al-Fatihah", 1),
    JuzInfo(2, "Al-Baqarah", 142),
    JuzInfo(3, "Al-Baqarah", 253),
    JuzInfo(4, "Ali 'Imran", 93),
    JuzInfo(5, "An-Nisa", 24),
    JuzInfo(6, "An-Nisa", 148),
    JuzInfo(7, "Al-Ma'idah", 83),
    JuzInfo(8, "Al-An'am", 111),
    JuzInfo(9, "Al-A'raf", 88),
    JuzInfo(10, "Al-A'raf", 207),
    JuzInfo(11, "At-Tawbah", 1),
    JuzInfo(12, "Hud", 6),
    JuzInfo(13, "Yusuf", 53),
    JuzInfo(14, "Ar-Ra'd", 1),
    JuzInfo(15, "An-Nahl", 1),
    JuzInfo(16, "Al-Isra", 1),
    JuzInfo(17, "Al-Kahf", 75),
    JuzInfo(18, "Maryam", 1),
    JuzInfo(19, "Ta-Ha", 1),
    JuzInfo(20, "Al-Anbiya", 1),
    JuzInfo(21, "Al-Mu'minun", 1),
    JuzInfo(22, "Ash-Shu'ara", 1),
    JuzInfo(23, "An-Naml", 56),
    JuzInfo(24, "Al-Qasas", 51),
    JuzInfo(25, "Al-'Ankabut", 46),
    JuzInfo(26, "Ar-Rum", 1),
    JuzInfo(27, "Ya-Sin", 28),
    JuzInfo(28, "As-Saffat", 145),
    JuzInfo(29, "Al-Fath", 1),
    JuzInfo(30, "An-Naba", 1)
)