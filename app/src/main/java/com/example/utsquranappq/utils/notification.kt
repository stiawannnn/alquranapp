package com.example.utsquranappq.utils

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.utsquranappq.work.QuranReminderReceiver
import java.util.Calendar

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel("quran_channel", "Quran Reminder", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Pengingat Al-Qur'an dan Adzan"
            setShowBadge(true)
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            // Tidak ada setSound di sini untuk menghindari suara default
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}

fun scheduleReminder(context: Context, type: String, time: String, requestCode: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, QuranReminderReceiver::class.java).apply {
        putExtra("type", type)
        putExtra("prayerTime", time)
        if (type == "adhan") putExtra("prayerName", getPrayerName(time))
        if (type == "adhan") putExtra("isAdhanEnabled", true)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val calendar = Calendar.getInstance().apply {
        val (hours, minutes) = time.split(":").map { it.toInt() }
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
    }

    val triggerTime = calendar.timeInMillis
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

fun scheduleAdhan(context: Context) {
    val prayerTimes = listOf("04:59" to "Subuh", "12:20" to "Dzuhur", "15:30" to "Ashar", "18:23" to "Maghrib", "19:32" to "Isya")
    prayerTimes.forEachIndexed { index, (time, _) -> scheduleReminder(context, "adhan", time, index + 1) }
}

fun cancelAdhan(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val prayerTimes = listOf("04:59" to "Subuh", "12:20" to "Dzuhur", "15:30" to "Ashar", "18:23" to "Maghrib", "19:32" to "Isya")
    prayerTimes.forEachIndexed { index, (time, name) ->
        val intent = Intent(context, QuranReminderReceiver::class.java).apply {
            putExtra("type", "adhan")
            putExtra("prayerTime", time)
            putExtra("prayerName", name)
            putExtra("isAdhanEnabled", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, index + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}

private fun getPrayerName(time: String): String = when (time) {
    "04:59" -> "Subuh"
    "12:20" -> "Dzuhur"
    "15:30" -> "Ashar"
    "18:23" -> "Maghrib"
    "19:32" -> "Isya"
    else -> "Unknown"
}