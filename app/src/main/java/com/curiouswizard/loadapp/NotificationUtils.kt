package com.curiouswizard.loadapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

fun NotificationManager.sendNotification(applicationContext: Context, downloadName: String, downloadStatus: String) {
    // Create the content intent for the notification, which launches this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra("name", downloadName)
    detailIntent.putExtra("status", downloadStatus)

    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(applicationContext, "channelId")
            .setSmallIcon(R.drawable.ic_assistant_black)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.download_finished))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

            .addAction(
                    R.drawable.baseline_cloud_download_24,
                    applicationContext.getString(R.string.check_status),
                    detailPendingIntent
            )

            .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID,builder.build())
}

/**
 * Cancels all notifications.
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}