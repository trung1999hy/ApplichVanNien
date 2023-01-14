package com.example.universalcalendar.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Festival(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("location")
    @Expose
    val location: String,

    @SerializedName("timeEvent")
    @Expose
    val timeEvent: String,

    @SerializedName("address")
    @Expose
    val address: String,

    @SerializedName("container")
    @Expose
    val container: String,

    @SerializedName("month")
    @Expose
    val month: String,


    ) : Serializable {
}