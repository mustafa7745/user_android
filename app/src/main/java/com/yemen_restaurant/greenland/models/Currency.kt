package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyModel (
    val project_currency_id: String,
    val currency_price: String,
    val currency_name: String,
    val currency_type: String
)