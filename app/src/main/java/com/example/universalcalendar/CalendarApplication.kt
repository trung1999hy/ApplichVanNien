package com.example.universalcalendar

import android.app.Application

class CalendarApplication : Application() {

    companion object {
        var instance: CalendarApplication? = null
        fun context() = instance!!.applicationContext!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}