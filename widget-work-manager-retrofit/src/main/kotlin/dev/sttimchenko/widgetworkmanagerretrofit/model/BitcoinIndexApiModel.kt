package dev.sttimchenko.widgetworkmanagerretrofit.model


import com.google.gson.annotations.SerializedName

data class BitcoinIndex(
    @SerializedName("bpi")
    val bpi: Bpi,
    @SerializedName("chartName")
    val chartName: String,
    @SerializedName("time")
    val time: Time
)

data class Bpi(
    @SerializedName("EUR")
    val euroRate: Rate,
    @SerializedName("GBP")
    val poundsRate: Rate,
    @SerializedName("USD")
    val usdRate: Rate
)

data class Rate(
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("rate")
    val rate: String,
    @SerializedName("rate_float")
    val rateFloat: Double,
    @SerializedName("symbol")
    val symbol: String
)

data class Time(
    @SerializedName("updated")
    val updated: String
)