package com.example.universalcalendar.ui.service

import android.R
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.universalcalendar.extensions.AlarmUtils
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.HomeActivity


class EventReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(p0: Context?, p1: Intent?) {
        val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) p1?.getSerializableExtra(AlarmUtils.KEY_EVENT_INFO, Event::class.java) as Event
        else p1?.getSerializableExtra("EVENT_INFO") as? Event
        // tapResultIntent gets executed when user taps the notification
        val notiContent = event?.title + "\n" + "${event?.timeStart} - ${event?.timeEnd}"
        val tapResultIntent = Intent(p0, HomeActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity( p0,0,tapResultIntent,FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val notification = p0?.let {
            NotificationCompat.Builder(it, "calendar_universal")
                .setContentTitle("Lịch Thiên niên kỷ")
                .setContentText(notiContent)
                .setSmallIcon(R.mipmap.sym_def_app_icon)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = p0?.let { NotificationManagerCompat.from(it) }
        notification?.let { event?.let { it1 -> notificationManager?.notify(it1.id, it) } }
    }
}