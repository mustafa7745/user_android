package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OfferProductsModel (
    val id: String,
    val product: ProductModel,
    val productQuantity : String
)