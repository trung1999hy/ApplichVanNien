package com.example.universalcalendar.widgets

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.universalcalendar.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {

     val textView: TextView = view.findViewById(R.id.calendarDayText)
     val imageView1: ImageView = view.findViewById(R.id.iv_day_event_1)
     val imageView2: ImageView = view.findViewById(R.id.iv_day_event_2)
     lateinit var day: CalendarDay
     lateinit var weekDay: WeekDay
     var onClickDate: (() -> Unit)? = null

     init {
          view.setOnClickListener {
               onClickDate?.invoke()
          }
     }
}