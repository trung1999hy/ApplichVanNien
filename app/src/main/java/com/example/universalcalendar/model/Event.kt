package com.example.universalcalendar.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Event(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("id_content")
    val idContent: Int = 0,

    @SerializedName("lunar")
    val isLunar: Boolean = true,

    @SerializedName("dayLunar")
    val dayLunar: Int = 0,

    @SerializedName("daySolar")
    val daySolar: Int,

    @SerializedName("monthLunar")
    val monthLunar: Int = 0,

    @SerializedName("monthSolar")
    val monthSolar: Int,

    @SerializedName("yearLunar")
    val yearLunar: Int = 0,

    @SerializedName("yearSolar")
    val yearSolar: Int,

    @SerializedName("title")
    val title: String?,

    @SerializedName("topic")
    val topic: String? = "",

    @SerializedName("timeStart")
    val timeStart: String?,

    @SerializedName("timeEnd")
    val timeEnd: String?,

    @SerializedName("address")
    val address: String? = "",

    @SerializedName("typeLoop")
    val typeLoop: Int = 0,

    @SerializedName("timeNotify")
    val timeNotify: Int = 0
) : Serializable