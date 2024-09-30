package com.yemen_restaurant.greenland.models


import kotlinx.serialization.Serializable

@Serializable
data class OrderStatus (
    val id: String,
    val situation: String,
    val orderId: String,
    val createdAt: String
)
