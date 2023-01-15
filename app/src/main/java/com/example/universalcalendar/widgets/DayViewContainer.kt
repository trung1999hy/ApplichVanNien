package com.example.universalcalendar.widgets

import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import com.example.universalcalendar.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {

     val textView: TextView = view.findViewById(R.id.calendarDayText)
     val imageEventTop: LinearLayout = view.findViewById(R.id.iv_day_event)
     lateinit var day: CalendarDay
     lateinit var weekDay: WeekDay
     var onClickDate: (() -> Unit)? = null

     init {
          view.setOnClickListener {
               onClickDate?.invoke()
          }
     }

     fun showImageDob() {
          val dobView = View.inflate(view.context, R.layout.item_birthday_balloon, null)
          imageEventTop.addView(dobView)
          val param = dobView.layoutParams
          param.width = LayoutParams.MATCH_PARENT
          param.height = LayoutParams.MATCH_PARENT
          dobView.layoutParams = param
          imageEventTop.visibility = View.VISIBLE
     }

}