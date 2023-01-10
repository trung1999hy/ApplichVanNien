package com.example.universalcalendar.widgets

import android.view.View
import com.example.universalcalendar.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {

     val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
     lateinit var day: CalendarDay
     lateinit var weekDay: WeekDay
     var onClickDate: (() -> Unit)? = null

     init {
          view.setOnClickListener {
               onClickDate?.invoke()
          }
     }
}