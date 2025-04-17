package com.example.quranappbystiawan.activity.homescreen

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getCurrentTime(): String {
    val locale = Locale("id", "ID")
    val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm:ss 'WIB'", locale)
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}