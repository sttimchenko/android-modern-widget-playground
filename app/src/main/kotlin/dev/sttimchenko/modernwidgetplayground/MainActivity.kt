package dev.sttimchenko.modernwidgetplayground

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.sttimchenko.widgetworkmanagerretrofit.BitcoinWidgetProvider

class MainActivity : AppCompatActivity() {

    private val pinWidgetButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_pin_widget)
    }

    private val updateWidgetsButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_update_widgets)
    }

    private val widgetStatusButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.button_widget_status)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateWidgetsStatusButtonText()
    }

    override fun onResume() {
        super.onResume()

        pinWidgetButton.setOnClickListener {
            requestToPinWidget()
        }

        updateWidgetsButton.setOnClickListener {
            triggerWidgetsUpdate()
        }

        widgetStatusButton.setOnClickListener {
            setWidgetsEnabled(areWidgetsEnabled().not())
            updateWidgetsStatusButtonText()
        }
    }

    override fun onPause() {
        super.onPause()

        pinWidgetButton.setOnClickListener(null)
        updateWidgetsButton.setOnClickListener(null)
        widgetStatusButton.setOnClickListener(null)
    }

    private fun requestToPinWidget() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val providerComponent = ComponentName(this, BitcoinWidgetProvider::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(providerComponent, null, null)
        }
    }

    private fun triggerWidgetsUpdate() {
        sendBroadcast(BitcoinWidgetProvider.refreshIntent(this))
    }

    private fun areWidgetsEnabled(): Boolean {
        val providerComponent = ComponentName(this, BitcoinWidgetProvider::class.java)
        val enabledSetting = packageManager.getComponentEnabledSetting(providerComponent)

        return enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                || enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
    }

    private fun setWidgetsEnabled(enabled: Boolean) {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val providerComponent = ComponentName(this, BitcoinWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(providerComponent)

        when {
            enabled -> packageManager.setComponentEnabledSetting(
                providerComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            enabled.not() && appWidgetIds.isNotEmpty() -> Toast.makeText(
                this,
                "Cannot disable widgets provider as it will compromise added widgets!",
                Toast.LENGTH_LONG
            ).show()

            enabled.not() -> packageManager.setComponentEnabledSetting(
                providerComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun updateWidgetsStatusButtonText() {
        val widgetStatus = if (areWidgetsEnabled()) {
            "Disable widgets"
        } else {
            "Enable widgets"
        }

        widgetStatusButton.text = widgetStatus
    }
}