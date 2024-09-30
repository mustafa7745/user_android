package com.yemen_restaurant.greenland.models


import kotlinx.serialization.Serializable

@Serializable
data class OrderProductsModel (
    val orderId: String,
    val products: List<OrderProductModel>,
    val finalPrice: Int,
    val productsFinalPrice: Int,
    val discount: DiscountModel?,
    val delivery: DeliveryModel?
)