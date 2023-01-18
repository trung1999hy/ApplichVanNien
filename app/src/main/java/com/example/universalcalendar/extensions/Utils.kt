package com.example.universalcalendar.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.IOException

object Utils {
    const val SUBJECT = "Lịch Thiên niên kỷ"
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

    fun goToGooglePlay(context: Context, appPackageName: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun shareApp(context: Context, appPackage: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plan"
        val shareBody =
            "https://play.google.com/store/apps/details?id=" + appPackage.trim { it <= ' ' }
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "share to"))
    }
}