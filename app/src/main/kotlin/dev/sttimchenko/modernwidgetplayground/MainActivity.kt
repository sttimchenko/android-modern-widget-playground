package dev.sttimchenko.modernwidgetplayground

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.sttimchenko.widgetworkmanagerretrofit.BitcoinWidgetProvider

class MainActivity : AppCompatActivity() {

    private val pinWidgetButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_pin_widget)
    }

    private val updateWidgetsButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_update_widgets)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        pinWidgetButton.setOnClickListener {
            requestToPinWidget()
        }

        updateWidgetsButton.setOnClickListener {
            triggerWidgetsUpdate()
        }
    }

    override fun onPause() {
        super.onPause()

        pinWidgetButton.setOnClickListener(null)
        updateWidgetsButton.setOnClickListener(null)
    }

    private fun requestToPinWidget() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, BitcoinWidgetProvider::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(myProvider, null, null)
        }
    }

    private fun triggerWidgetsUpdate() {
        sendBroadcast(BitcoinWidgetProvider.refreshIntent(this))
    }
}