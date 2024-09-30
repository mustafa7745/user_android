package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderSituation (
    val order_situation_id: String,
    val order_situation: String,
    val order_situation_created_at: String,
    val order_situation_updated_at: String
)