package com.example.universalcalendar.extensions

import android.content.Context
import java.io.IOException

object Utils {
   fun getDataFromAsset(context: Context, fileName: String): String? {
       val jsonString: String
       try {
           jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
       } catch (IOException: IOException) {
           IOException.printStackTrace()
           return null
       }
       return jsonString
   }

    fun formatDateTime() {

    }
}