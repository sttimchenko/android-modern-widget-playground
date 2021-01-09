package dev.sttimchenko.widgetworkmanagerretrofit.work

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.sttimchenko.widgetworkmanagerretrofit.BitcoinWidgetProvider
import dev.sttimchenko.widgetworkmanagerretrofit.dependencies.DependenciesFactory
import dev.sttimchenko.widgetworkmanagerretrofit.model.WidgetBitcoinIndexModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IndexRefreshWorker(
    ctx: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(ctx, workerParams) {

    private val bitcoinIndexApi = DependenciesFactory.buildBitcoinIndexApi()
    private val widgetDataStorage = DependenciesFactory.buildWidgetsDataStorage(applicationContext)
    private val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        Log.d("IndexRefreshWorker", "Doing work...")

        return withContext(Dispatchers.IO) {
            val provider = ComponentName(
                applicationContext,
                BitcoinWidgetProvider::class.java
            )

            val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)
            val bitcoinIndex = bitcoinIndexApi.getCurrentBitcoinPriceIndex()

            val widgetsData = appWidgetIds.map { widgetId ->
                WidgetBitcoinIndexModel(
                    widgetId,
                    bitcoinIndex.time.updated,
                    bitcoinIndex.bpi.usdRate.rateFloat.toInt(),
                    "$"
                )
            }

            widgetDataStorage.saveWidgetsData(widgetsData)

            applicationContext.sendBroadcast(BitcoinWidgetProvider.updateIntent(applicationContext))

            return@withContext Result.success()
        }
    }
}