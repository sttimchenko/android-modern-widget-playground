package dev.sttimchenko.widgetworkmanagerretrofit

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.sttimchenko.widgetworkmanagerretrofit.data.WidgetDataStorage
import dev.sttimchenko.widgetworkmanagerretrofit.dependencies.DependenciesFactory
import dev.sttimchenko.widgetworkmanagerretrofit.work.IndexRefreshWorker
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

private const val WORK_TAG_KEY = "BITCOIN_WIDGET_WORK"
private const val SCHEDULE_REFRESH_KEY = "SCHEDULE_REFRESH_KEY"

private const val WORK_REPEAT_IN_MINUTES = 15L

class BitcoinWidgetProvider : AppWidgetProvider() {

    companion object {
        fun updateIntent(context: Context) =
            Intent(context, BitcoinWidgetProvider::class.java).apply {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val provider = ComponentName(context, BitcoinWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)

                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }

        fun refreshIntent(context: Context) = updateIntent(context).apply {
            putExtra(SCHEDULE_REFRESH_KEY, true)
        }
    }

    private lateinit var widgetDataStorage: WidgetDataStorage

    private var shouldScheduleUpdate: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WorkManagerRetrofitWidgetProvider", "onReceive: action = ${intent.action}")

        shouldScheduleUpdate = intent.getBooleanExtra(SCHEDULE_REFRESH_KEY, false)

        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("WorkManagerRetrofitWidgetProvider", "onUpdate: ids = ${appWidgetIds.joinToString()}")

        initDependencies(context)

        val widgetsData = runBlocking {
            widgetDataStorage.getAllWidgetsData()
        }

        appWidgetIds.forEach { appWidgetId ->
            val widgetData = widgetsData.firstOrNull { it.widgetId == appWidgetId }

            if (widgetData == null) {
                shouldScheduleUpdate = true
            }

            appWidgetManager.updateAppWidget(
                appWidgetId,
                WidgetViewBuilder.build(context, widgetData)
            )
        }

        if (shouldScheduleUpdate) {
            scheduleUpdate(context)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        Log.d("WorkManagerRetrofitWidgetProvider", "onAppWidgetOptionsChanged: id = $appWidgetId, options = ${newOptions.keySet().map { newOptions.get(it) }.joinToString()}")

        initDependencies(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        Log.d("WorkManagerRetrofitWidgetProvider", "onDeleted: ids = ${appWidgetIds.joinToString()}")

        initDependencies(context)

        appWidgetIds.forEach {
            runBlocking {
                widgetDataStorage.removeWidgetData(it)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        Log.d("WorkManagerRetrofitWidgetProvider", "onEnabled")

        initDependencies(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        Log.d("WorkManagerRetrofitWidgetProvider", "onDisabled")

        initDependencies(context)
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)

        Log.d("WorkManagerRetrofitWidgetProvider", "onRestored")

        initDependencies(context)
    }

    private fun initDependencies(context: Context) {
        widgetDataStorage = DependenciesFactory.buildWidgetsDataStorage(context)
    }

    private fun scheduleUpdate(context: Context) {
        val workRequest =
            PeriodicWorkRequestBuilder<IndexRefreshWorker>(WORK_REPEAT_IN_MINUTES, TimeUnit.MINUTES)
                .addTag(WORK_TAG_KEY)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG_KEY,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}