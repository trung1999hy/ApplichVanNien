package com.example.universalcalendar.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Customs (
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("image")
    @Expose
    val image: String,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("container")
    @Expose
    val container: String,
) : Serializable {
}