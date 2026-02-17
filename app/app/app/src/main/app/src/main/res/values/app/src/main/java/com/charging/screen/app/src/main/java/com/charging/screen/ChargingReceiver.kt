package com.charging.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class ChargingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_POWER_CONNECTED) return

        // 1) Megpróbáljuk azonnal megnyitni (egyes rendszereken ezt korlátozhatják)
        try {
            val i = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(i)
            return
        } catch (_: Exception) {
            // 2) Ha nem engedi, full-screen értesítéssel próbáljuk
        }

        showFullscreenNotification(context)
    }

    private fun showFullscreenNotification(context: Context) {
        val nm = context.getSystemService<NotificationManager>() ?: return
        val channelId = "charging_screen_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Charging Screen",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pi = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle("Charging")
            .setContentText("Tap to open charging screen")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SYSTEM)
            .setAutoCancel(true)
            .setFullScreenIntent(pi, true)
            .setContentIntent(pi)
            .build()

        nm.notify(1002, notif)
    }
}
