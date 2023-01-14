package com.example.universalcalendar.ui.feature.setting.knowledge

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.model.PTBusiness
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class KnowledgeViewModel : ViewModel(){
    private val mListKnowledge = ArrayList<PTBusiness>()
    val listKnowledge = MutableLiveData<List<PTBusiness>>()
    fun fetchDataFromJson(context: Context) {
        mListKnowledge.clear()
        val jsonString = Utils.getDataFromAsset(context, Constant.PT_KNOWLEDGE)
        val typeToken = object : TypeToken<List<PTBusiness>>() {}.type
        val listCustomsFromJson =
            Gson().fromJson<List<PTBusiness>>(jsonString, typeToken)
        mListKnowledge.addAll(listCustomsFromJson)
        listKnowledge.value = mListKnowledge
    }
}