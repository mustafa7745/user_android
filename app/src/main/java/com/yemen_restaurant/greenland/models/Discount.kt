package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class DiscountModel (
    val order_discount_id: String,
    val order_discount_type: String,
    val order_discount_order_id: String,
    val order_discount_amount: String,
    val order_discount_created_at: String
)