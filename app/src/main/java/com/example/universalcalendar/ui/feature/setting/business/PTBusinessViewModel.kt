package com.example.universalcalendar.ui.feature.setting.business

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.model.PTBusiness
import com.example.universalcalendar.model.Vows
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PTBusinessViewModel : ViewModel(){
    private val mListPTBusiness = ArrayList<PTBusiness>()
    val listPTBusiness = MutableLiveData<List<PTBusiness>>()
    fun fetchDataFromJson(context: Context) {
        mListPTBusiness.clear()
        val jsonString = Utils.getDataFromAsset(context, Constant.PT_BUSINESS)
        val typeToken = object : TypeToken<List<PTBusiness>>() {}.type
        val listCustomsFromJson =
            Gson().fromJson<List<PTBusiness>>(jsonString, typeToken)
        mListPTBusiness.addAll(listCustomsFromJson)
        listPTBusiness.value = mListPTBusiness
    }
}