package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object HarmonyGameNotifier {

    const val EXTRA_GENERATED_GAME_ID = "generated_game_id"
    const val CHANNEL_GENERATED_GAMES = "harmony_generated_games"
    private const val NOTIFICATION_ID_BASE = 42000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Harmony Persönliche Spiele"
            val descriptionText = "Benachrichtigungen über neue KI-Spiele in Für dich"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_GENERATED_GAMES, name, importance).apply {
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

    fun notifyNewGeneratedGame(
        context: Context,
        gameId: String,
        title: String,
        emoji: String
    ) {
        try {
            createNotificationChannel(context)

            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_GENERATED_GAME_ID, gameId)
                putExtra("open_tab", 1)
            }

            val requestCode = gameId.hashCode()
            val pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notifTitle = "✨ Neues Spiel für euch"
            val notifText = "$emoji $title wartet in Für dich"
            val expandedText = "Euer Harmony Brain hat ein neues Spiel für euch erstellt: $emoji $title"

            val largeIconBitmap = createEmojiBadgeBitmap(emoji)

            val builder = NotificationCompat.Builder(context, CHANNEL_GENERATED_GAMES)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIconBitmap)
                .setColor(Color.parseColor("#E056FD"))
                .setContentTitle(notifTitle)
                .setContentText(notifText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(0, "Jetzt spielen", pendingIntent)
                .setVibrate(longArrayOf(0, 150, 100, 150))

            val notifId = NOTIFICATION_ID_BASE + (requestCode % 1000)
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (_: Throwable) {
            // Graceful safety
        }
    }

    private fun createEmojiBadgeBitmap(emoji: String): Bitmap {
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, size.toFloat(), size.toFloat(),
                Color.parseColor("#FF6584"), Color.parseColor("#7028E4"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, circlePaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 56f
            textAlign = Paint.Align.CENTER
        }
        val yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(emoji, size / 2f, yPos, textPaint)

        return bitmap
    }
}
