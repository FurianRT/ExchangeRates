package com.furianrt.exchangerates.data.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RatesApiResponse(
    @SerializedName("quotes") val rates: List<Rate>
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Rate))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(rates)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RatesApiResponse> {
        override fun createFromParcel(parcel: Parcel): RatesApiResponse {
            return RatesApiResponse(parcel)
        }

        override fun newArray(size: Int): Array<RatesApiResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class Rate(
    @SerializedName("base_currency") val baseCurrency: String,
    @SerializedName("quote_currency") val quoteCurrency: String,
    @SerializedName("open_time") val date: String,
    @SerializedName("average_midpoint") val value: Float
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(baseCurrency)
        parcel.writeString(quoteCurrency)
        parcel.writeString(date)
        parcel.writeFloat(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rate> {
        override fun createFromParcel(parcel: Parcel): Rate {
            return Rate(parcel)
        }

        override fun newArray(size: Int): Array<Rate?> {
            return arrayOfNulls(size)
        }
    }
}