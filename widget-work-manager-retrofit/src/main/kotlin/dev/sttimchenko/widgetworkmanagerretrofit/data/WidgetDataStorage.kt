package dev.sttimchenko.widgetworkmanagerretrofit.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.sttimchenko.widgetworkmanagerretrofit.model.WidgetBitcoinIndexModel
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val KEY_WIDGETS_DATA = "WidgetDataStorage.KEY_WIDGETS_DATA"

class WidgetDataStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    private val listOfDataType: Type
        get() = object : TypeToken<ArrayList<WidgetBitcoinIndexModel>>() {}.type

    suspend fun getAllWidgetsData(): List<WidgetBitcoinIndexModel> =
        suspendCoroutine { continuation ->
            val savedData = sharedPreferences.getString(KEY_WIDGETS_DATA, "") ?: ""

            val result = if (savedData.isEmpty()) {
                emptyList()
            } else {
                gson.fromJson<List<WidgetBitcoinIndexModel>>(savedData, listOfDataType)
            }

            continuation.resume(result)
        }

    suspend fun getWidgetData(widgetId: Int): WidgetBitcoinIndexModel? =
        suspendCoroutine { continuation ->
            val savedData = sharedPreferences.getString(KEY_WIDGETS_DATA, "") ?: ""

            val result = if (savedData.isEmpty()) {
                null
            } else {
                gson.fromJson<List<WidgetBitcoinIndexModel>>(
                    sharedPreferences.getString(
                        KEY_WIDGETS_DATA,
                        ""
                    ), listOfDataType
                ).find { it.widgetId == widgetId }
            }

            continuation.resume(result)
        }

    suspend fun saveWidgetsData(widgetsData: List<WidgetBitcoinIndexModel>) =
        suspendCoroutine<Unit> { continuation ->
            val result = sharedPreferences.edit()
                .putString(KEY_WIDGETS_DATA, gson.toJson(widgetsData))
                .commit()

            continuation.resume(Unit)
        }

    suspend fun removeWidgetData(widgetId: Int) {
        val updatedData =
            getAllWidgetsData().filter { widgetData -> widgetData.widgetId != widgetId }

        saveWidgetsData(updatedData)
    }
}