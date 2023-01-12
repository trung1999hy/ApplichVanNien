package com.example.universalcalendar.extensions

import android.location.Location
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.example.universalcalendar.common.Constant
import java.text.SimpleDateFormat
import java.util.Date

fun TextView.formatDateTime(dateTime: Date) {
    Handler().postDelayed({
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        text = simpleDateFormat.format(dateTime).toString()
    }, Constant.TIME_MILLISECOND_1000L)
}

fun View.visible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.gone(): Boolean {
    return this.visibility == View.GONE
}
