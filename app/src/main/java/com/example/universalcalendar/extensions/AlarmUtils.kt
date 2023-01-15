package com.example.universalcalendar.extensions

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.service.EventReceiver
import java.time.LocalDateTime
import java.util.*


object AlarmUtils {

    const val KEY_EVENT_INFO = "event_info"

    @SuppressLint("UnspecifiedImmutableFlag")
    fun create(context: Context?, date: LocalDateTime, event: Event) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java)
        intent.putExtra(KEY_EVENT_INFO, event)
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mainActivityIntent = Intent(context, HomeActivity::class.java)
        val basicPendingIntent = PendingIntent.getActivity(context, event.id, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.YEAR, date.year)
//        calendar.add(Calendar.MONTH, date.monthValue)
//        calendar.add(Calendar.DAY_OF_MONTH, date.dayOfMonth)
//        calendar.add(Calendar.HOUR, date.hour)
//        calendar.add(Calendar.MINUTE, date.minute)
        val hourEvent = if (date.hour < 10) "0${date.hour}" else date.hour.toString()
        val minuteEvent = if (date.minute < 10) "0${date.minute}" else date.minute.toString()
        val monthEvent = if (date.monthValue < 10) "0${date.monthValue}" else date.monthValue.toString()
        val dayEvent = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else date.dayOfMonth.toString()
        val date = DateUtils.convertStringToDate(
            DateUtils.DATE_EVENT_REGISTER,
            "${date.year}$monthEvent$dayEvent$hourEvent$minuteEvent"
        ) ?: Date()
        val clockInfo = AlarmManager.AlarmClockInfo(date.time, basicPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)
    }

    private fun removeAlarm(context: Context?, event: Event){
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java)
        // It is not necessary to add putExtra
        intent.putExtra(KEY_EVENT_INFO, event)
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}