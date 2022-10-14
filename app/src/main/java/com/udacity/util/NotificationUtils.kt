package com.udacity.utils

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.DetailActivity
import com.udacity.R


// Notification ID.
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    applicationContext: Context,
    download: String,
    status: String
) {

    val notificationManager = ContextCompat.getSystemService(
        applicationContext,
        NotificationManager::class.java
    ) as NotificationManager

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra("download", download)
    detailIntent.putExtra("status", status)

    val detailPendingIntent = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(detailIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    } as PendingIntent

    val action = NotificationCompat.Action(
        R.drawable.ic_assistant_black_24dp,
        applicationContext.getString(R.string.notification_button),
        detailPendingIntent
    )

    val contentIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext, "channelId"
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.finished_download))
        .setContentIntent(contentPendingIntent)
        .addAction(action)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notificationManager.notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}