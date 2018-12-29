package com.awareframework.android.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.awareframework.android.core.R

/**
 * Helper class for notifications.
 *
 * @author  sercant
 * @date 15/08/2018
 */
class NotificationUtil {
    companion object {
        const val AWARE_NOTIFICATION_ID = "AWARE_NOTIFICATION_ID"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                        AWARE_NOTIFICATION_ID,
                        context.resources.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.resources.getString(R.string.aware_description)
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}