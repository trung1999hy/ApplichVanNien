package com.example.universalcalendar.widgets

import android.view.View
import com.example.universalcalendar.databinding.MonthCalendarDayLayoutBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {

     val textView = MonthCalendarDayLayoutBinding.bind(view).calendarDayText
     lateinit var day: CalendarDay
     var onClickDate: (() -> Unit)? = null

     init {
          view.setOnClickListener {
               onClickDate?.invoke()
          }
     }
}