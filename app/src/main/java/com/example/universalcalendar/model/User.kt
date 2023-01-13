package com.example.universalcalendar.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String?,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String?,

    @SerializedName("email")
    val email: String?
)