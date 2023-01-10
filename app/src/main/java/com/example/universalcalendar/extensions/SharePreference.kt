package com.example.universalcalendar.extensions

import android.content.Context
import android.content.SharedPreferences
import com.example.universalcalendar.CalendarApplication
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.model.Quotation
import com.example.universalcalendar.model.Quote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharePreference {
    companion object {
        const val MY_PREFERENCES = "MyPrefs"
    }
    val mPrefs: SharedPreferences = CalendarApplication.context().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)

    fun getQuote(): List<Quotation>? {
        val quoteKey = mPrefs.getString(Constant.KEY_QUOTE, "") ?: ""
        if (quoteKey.isEmpty()) {
            return null
        }
        return Gson().fromJson(quoteKey, object : TypeToken<List<Quotation>>() {}.type)
    }

    fun setQuote(listQuotation: List<Quotation>) {
        mPrefs.edit().putString(Constant.KEY_QUOTE, Gson().toJson(listQuotation)).apply()
    }
}
