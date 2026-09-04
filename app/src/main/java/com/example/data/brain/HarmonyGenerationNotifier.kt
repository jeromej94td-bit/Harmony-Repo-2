package com.example.data.brain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object HarmonyGenerationNotifier {

    const val CHANNEL_ID = "harmony_brain_generated_games"
    private const val NOTIFICATION_ID = 20261

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Harmony Brain Spiele"
            val descriptionText = "Benachrichtigungen über neue persönliche KI-Spiele"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.parseColor("#E056FD")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun notifyNewGameCreated(context: Context, newGamesCount: Int = 1) {
        try {
            createNotificationChannel(context)

            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_tab", 1)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "🧠 Harmony hat etwas Neues für euch"
            val text = "Ein persönliches Spiel wartet auf euch – passend zu euren bisherigen Antworten."
            val expandedText = "Euer Harmony Brain hat aus euren gemeinsamen Antworten eine neue Frage erstellt."

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#E056FD"))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 150, 100, 150))

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (_: Throwable) {
            // Graceful fallback: never crash on notification dispatch
        }
    }
}
