package com.example.universalcalendar

import android.app.Application

class CalendarApplication : Application() {
    companion object {
        private lateinit var instance: CalendarApplication

        fun getInstance(): CalendarApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}