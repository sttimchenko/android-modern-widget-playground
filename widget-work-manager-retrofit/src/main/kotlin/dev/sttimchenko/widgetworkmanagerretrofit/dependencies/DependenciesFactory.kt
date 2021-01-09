package dev.sttimchenko.widgetworkmanagerretrofit.dependencies

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.sttimchenko.widgetworkmanagerretrofit.data.BitcoinIndexApi
import dev.sttimchenko.widgetworkmanagerretrofit.data.WidgetDataStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_BITCOIN_INDEX_URL = "https://api.coindesk.com"
private const val WIDGETS_DATA_PREFERENCES_NAME = "dev.sttimchenko.widgetworkmanagerretrofit.widgets_preferences"

object DependenciesFactory {

    fun buildWidgetsDataStorage(ctx: Context): WidgetDataStorage =
        WidgetDataStorage(buildPreferences(ctx), buildGson())

    private fun buildPreferences(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(WIDGETS_DATA_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private fun buildGson(): Gson = GsonBuilder().create()

    fun buildBitcoinIndexApi(): BitcoinIndexApi {
        return buildRetrofit(BASE_BITCOIN_INDEX_URL).create(BitcoinIndexApi::class.java)
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder().build()

        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }
}