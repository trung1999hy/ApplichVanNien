package com.example.universalcalendar.ui.feature.setting.vows

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.model.Customs
import com.example.universalcalendar.model.Vows
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VowsViewModel : ViewModel() {
    private val mListVows = ArrayList<Vows>()
    val listVows = MutableLiveData<List<Vows>>()
    fun fetchDataFromJson(context: Context) {
        mListVows.clear()
        val jsonString = Utils.getDataFromAsset(context, Constant.VOWS)
        val typeToken = object : TypeToken<List<Vows>>() {}.type
        val listCustomsFromJson =
            Gson().fromJson<List<Vows>>(jsonString, typeToken)
        mListVows.addAll(listCustomsFromJson)
        listVows.value = mListVows
    }
}