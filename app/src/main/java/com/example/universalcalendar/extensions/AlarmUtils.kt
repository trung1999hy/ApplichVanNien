package com.example.universalcalendar.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.broadcasts.EventReceiver
import java.util.*


object AlarmUtils {

    const val KEY_EVENT_INFO = "event_info"
    const val KEY_EVENT_TIME = "event_time"

    fun create(context: Context?, calendar: Calendar, event: Event) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java)
        val mainActivityIntent = Intent(context, HomeActivity::class.java)
        val contentEvent = event.title
        intent.putExtra(KEY_EVENT_INFO, contentEvent)
        if (event.timeStart == event.timeEnd) {
            val timeEvent = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_LOCALE_FORMAT_2, event.timeStart),
                DateUtils.DATE_EVENT_NOTIFY_ALL_DAY
            )
            val timeAllDay = "Cả ngày, $timeEvent"
            intent.putExtra(KEY_EVENT_TIME, timeAllDay)
        } else {
            val timeStart = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, event.timeStart),
                DateUtils.DATE_EVENT_NOTIFY
            )
            val timeEnd = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, event.timeEnd),
                DateUtils.DATE_EVENT_NOTIFY
            )
            val timeEvent = "$timeStart - $timeEnd"
            intent.putExtra(KEY_EVENT_TIME, timeEvent)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val basicPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmClock = AlarmManager.AlarmClockInfo(calendar.timeInMillis, basicPendingIntent)
        alarmManager.setAlarmClock(alarmClock, pendingIntent )
    }

    private fun removeAlarm(context: Context?, event: Event){
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, EventReceiver::class.java)
//        // It is not necessary to add putExtra
//        intent.putExtra(KEY_EVENT_INFO, event)
//        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, PendingIntent.FLAG_IMMUTABLE)
//        alarmManager.cancel(pendingIntent)
    }
}