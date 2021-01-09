package dev.sttimchenko.widgetworkmanagerretrofit.data

import dev.sttimchenko.widgetworkmanagerretrofit.model.BitcoinIndex
import retrofit2.http.GET

interface BitcoinIndexApi {
    @GET("/v1/bpi/currentprice.json")
    suspend fun getCurrentBitcoinPriceIndex(): BitcoinIndex
}