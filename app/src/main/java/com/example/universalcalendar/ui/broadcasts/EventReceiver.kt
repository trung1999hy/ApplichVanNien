package com.example.universalcalendar.ui.broadcasts

import android.R
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.universalcalendar.extensions.AlarmUtils
import com.example.universalcalendar.ui.HomeActivity


class EventReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(context, HomeActivity::class.java)
        val content = intent?.getStringExtra(AlarmUtils.KEY_EVENT_INFO) ?: ""
        val time = intent?.getStringExtra(AlarmUtils.KEY_EVENT_TIME) ?: ""
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = getActivity(context, 0, i, 0)

        val builder = NotificationCompat.Builder(context!!, "lichthiennienky")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle("$content")
            .setContentText("$time")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager = NotificationManagerCompat.from(context)
        notificationManager!!.notify( 0, builder.build())
    }
}