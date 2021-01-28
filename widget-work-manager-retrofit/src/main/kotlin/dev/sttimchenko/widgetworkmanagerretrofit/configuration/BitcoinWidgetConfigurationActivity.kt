package dev.sttimchenko.widgetworkmanagerretrofit.configuration

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.sttimchenko.widgetworkmanagerretrofit.BitcoinWidgetProvider
import dev.sttimchenko.widgetworkmanagerretrofit.R

class BitcoinWidgetConfigurationActivity : AppCompatActivity() {

    private val triggerUpdateButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_trigger_update)
    }

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitcoin_widget_configuration)

        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setResult(
            RESULT_CANCELED,
            Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
        )
    }

    override fun onResume() {
        super.onResume()

        triggerUpdateButton.setOnClickListener {
            sendBroadcast(BitcoinWidgetProvider.updateIntent(this))

            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
            )

            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        triggerUpdateButton.setOnClickListener(null)
    }
}