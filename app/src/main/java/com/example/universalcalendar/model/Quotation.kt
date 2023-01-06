package com.example.universalcalendar.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Quotation(
    @SerializedName("author")
    @Expose
    val author: String?,

    @SerializedName("quote")
    @Expose
    val quote: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(quote)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quotation> {
        override fun createFromParcel(parcel: Parcel): Quotation {
            return Quotation(parcel)
        }

        override fun newArray(size: Int): Array<Quotation?> {
            return arrayOfNulls(size)
        }
    }
}