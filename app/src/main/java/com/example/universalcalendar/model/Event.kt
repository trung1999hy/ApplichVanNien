package com.example.universalcalendar.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("id_content")
    val idContent: Int,

    @SerializedName("lunar")
    val isLunar: Boolean,

    @SerializedName("dayLunar")
    val dayLunar: Int,

    @SerializedName("daySolar")
    val daySolar: Int,

    @SerializedName("monthLunar")
    val monthLunar: Int,

    @SerializedName("monthSolar")
    val monthSolar: Int,

    @SerializedName("yearLunar")
    val yearLunar: Int,

    @SerializedName("yearSolar")
    val yearSolar: Int,

    @SerializedName("title")
    val title: String?
)