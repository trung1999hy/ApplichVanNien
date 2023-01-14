package com.example.universalcalendar.ui.feature.setting.pt

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.model.Customs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CustomsViewModel : ViewModel() {
    private val mListCustoms = ArrayList<Customs>()
    val listCustoms = MutableLiveData<List<Customs>>()
    fun fetchDataFromJson(context: Context) {
        mListCustoms.clear()
        val jsonString = Utils.getDataFromAsset(context, Constant.CUSTOMS)
        val typeToken = object : TypeToken<List<Customs>>() {}.type
        val listCustomsFromJson =
            Gson().fromJson<List<Customs>>(jsonString, typeToken)
        mListCustoms.addAll(listCustomsFromJson)
        listCustoms.value = mListCustoms
    }
}