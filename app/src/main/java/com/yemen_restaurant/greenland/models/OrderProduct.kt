package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductModel (
    val id: String,
    val productId: String,
    val productName: String,
    val productPrice: String,
    val productQuantity:String,
    val createdAt: String,
    val updatedAt: String,
    val avg: String
)

@Serializable
data class OrderProductWithQntModel (
    val id: String,
    val qnt: String,
)