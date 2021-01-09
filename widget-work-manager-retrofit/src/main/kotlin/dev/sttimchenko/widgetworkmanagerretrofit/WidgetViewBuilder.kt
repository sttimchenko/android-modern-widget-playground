package dev.sttimchenko.widgetworkmanagerretrofit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import dev.sttimchenko.widgetworkmanagerretrofit.model.WidgetBitcoinIndexModel

object WidgetViewBuilder {
    fun build(context: Context, widgetBitcoinIndexModel: WidgetBitcoinIndexModel?): RemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context.packageManager.getLaunchIntentForPackage(context.packageName)),
            0
        )

        return RemoteViews(
            context.packageName,
            R.layout.widget_layout
        ).apply {
            if (widgetBitcoinIndexModel != null) {
                setCharSequence(
                    R.id.tv_price,
                    "setText",
                    context.getString(
                        R.string.widget_price_format,
                        widgetBitcoinIndexModel.currencySymbol,
                        widgetBitcoinIndexModel.rate
                    )
                )

                setCharSequence(
                    R.id.tv_timestamp,
                    "setText",
                    context.getString(
                        R.string.widget_timestamp_placeholder,
                        widgetBitcoinIndexModel.lastUpdatedTime
                    )
                )

                setOnClickPendingIntent(R.id.parent, pendingIntent)
            }

            toggleVisibility(widgetBitcoinIndexModel == null)
        }
    }

    private fun RemoteViews.toggleVisibility(isLoading: Boolean) {
        setViewVisibility(R.id.progress_indicator, isLoading.toVisibility())
        setViewVisibility(R.id.tv_price, isLoading.not().toVisibility())
        setViewVisibility(R.id.tv_timestamp, isLoading.not().toVisibility())
    }

    private fun Boolean.toVisibility(): Int = if (this) View.VISIBLE else View.GONE
}