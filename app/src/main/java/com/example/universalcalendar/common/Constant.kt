package com.example.universalcalendar.common

object Constant {
   const val INDEX_DEFAULT = 0

   object Calendar {
      const val NUMBER_ADD_MONTH_TO_CALENDAR = 100L
      const val NUMBER_ADD_WEEK_TO_CALENDAR = 100L
      val MAP_DAY_WEEK_TITLE: MutableMap<String, String> = mutableMapOf(
         "MONDAY" to "Thứ Hai",
         "TUESDAY" to "Thứ Ba",
         "WEDNESDAY" to "Thứ Tư",
         "THURSDAY" to "Thứ Năm",
         "FRIDAY" to "Thứ Sáu",
         "SATURDAY" to "Thứ Bảy",
         "SUNDAY" to "Chủ Nhật",
         "Mon" to "Hai",
         "Tue" to "Ba",
         "Wed" to "Tư",
         "Thu" to "Năm",
         "Fri" to "Sáu",
         "Sat" to "Bảy",
         "Sun" to "C.n",
      )
   }
}