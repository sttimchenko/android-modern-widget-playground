package dev.sttimchenko.widgetworkmanagerretrofit.model

data class WidgetBitcoinIndexModel(
    val widgetId: Int,
    val lastUpdatedTime: String,
    val rate: Int,
    val currencySymbol: String
)