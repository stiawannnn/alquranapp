package app.dev.quranappbystiawan.work

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.dev.quranappbystiawan.R
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.Calendar

class QuranReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return
        val prayerTime = intent.getStringExtra("prayerTime") ?: "Unknown"
        val prayerName = intent.getStringExtra("prayerName") ?: getPrayerName(prayerTime)
        val isAdhanEnabled = intent.getBooleanExtra("isAdhanEnabled", false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.w("QuranReminderReceiver", "No notification permission")
            return
        }

        when (type) {
            "quran" -> {
                showNotification(context, "Pengingat Harian", "Mari berhenti sejenak \n dan baca Al-Qur'an,\n waktunya telah tiba Pukul $prayerTime!", 1)
                scheduleReminder(context, type, prayerTime, 0)
            }
            "adhan" -> {
                val soundUri = android.net.Uri.parse("android.resource://${context.packageName}/${R.raw.adzhan}")
                showNotification(context, "Waktu Adzan $prayerName", "Adzan $prayerName pada pukul $prayerTime", prayerName.hashCode(), soundUri)
                if (isAdhanEnabled) playAdhanSound(context)
                scheduleReminder(context, type, prayerTime, prayerName.hashCode())
            }
        }
    }

    private fun showNotification(context: Context, title: String, text: String, id: Int, soundUri: android.net.Uri? = null) {
        val notification = NotificationCompat.Builder(context, "quran_channel")
            .setSmallIcon(R.drawable.qur5)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .apply { if (soundUri != null) setSound(soundUri) }
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
            Log.d("QuranReminderReceiver", "$title shown at $id")
        } catch (e: SecurityException) {
            Log.e("QuranReminderReceiver", "Notification error: ${e.message}")
        }
    }

    private fun playAdhanSound(context: Context) {
        val wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "QuranReminderReceiver::AdhanWakeLock")
            .apply { acquire(10 * 60 * 1000L) }

        try {
            val soundUri = android.net.Uri.parse("android.resource://${context.packageName}/${R.raw.adzhan}")
            MediaPlayer().apply {
                setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                setDataSource(context, soundUri)
                prepare()
                start()
                setOnCompletionListener { release(); wakeLock.release(); Log.d("QuranReminderReceiver", "Adhan completed") }
            }
        } catch (e: Exception) {
            Log.e("QuranReminderReceiver", "Adhan error: ${e.message}")
            wakeLock.release()
        }
    }

    private fun scheduleReminder(context: Context, type: String, time: String, requestCode: Int) {
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

    private fun getPrayerName(time: String): String = when (time) {
        "04:59" -> "Subuh"
        "12:20" -> "Dzuhur"
        "15:30" -> "Ashar"
        "18:23" -> "Maghrib"
        "19:32" -> "Isya"
        else -> "Unknown"
    }
}