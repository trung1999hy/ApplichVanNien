package com.example.universalcalendar.extensions

import android.util.Log
import com.example.universalcalendar.common.Strings.EMPTY
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    const val DATE_LOCALE_FORMAT = "yyyy-MM-dd"
    const val WEEK_DAY_FORMAT = "EEEE"
//    const val DATE_TITLE_FORMAT = "d tháng M, yyyy"

    fun convertStringToDate(format: String, dateStr: String?): Date? {
        if (dateStr.isNullOrEmpty())
            return null
        return try {
            SimpleDateFormat(format, Locale.ENGLISH).parse(dateStr)
        } catch (e: ParseException) {
//            Log.e("ConvertDateToString-ParseException: " + e.message)
            null
        }
    }

    fun convertDateToString(date: Date?, format: String): String? {
        if (date == null) return null
        return try {
            SimpleDateFormat(format, Locale.ENGLISH).format(date)
        } catch (e: ParseException) {
//            Log.e("ConvertDateToString-ParseException: " + e.message)
            null
        }
    }

}