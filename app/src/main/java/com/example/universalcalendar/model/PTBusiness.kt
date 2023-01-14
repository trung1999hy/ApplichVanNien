package com.example.universalcalendar.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PTBusiness(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("title")
    @Expose
    val title: String,


    @SerializedName("description")
    @Expose
    val description: String,

    ) : Serializable {
}