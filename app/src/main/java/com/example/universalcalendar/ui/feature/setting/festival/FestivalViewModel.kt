package com.example.universalcalendar.ui.feature.setting.festival

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.model.Customs
import com.example.universalcalendar.model.Festival
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FestivalViewModel : ViewModel() {
    private val mListFestival = ArrayList<Festival>()
    val listFestival = MutableLiveData<List<Festival>>()
    fun fetchDataFromJson(context: Context) {
        mListFestival.clear()
        val jsonString = Utils.getDataFromAsset(context, Constant.FESTIVAL)
        val typeToken = object : TypeToken<List<Festival>>() {}.type
        val listCustomsFromJson =
            Gson().fromJson<List<Festival>>(jsonString, typeToken)
        mListFestival.addAll(listCustomsFromJson)
        listFestival.value = mListFestival
    }
}