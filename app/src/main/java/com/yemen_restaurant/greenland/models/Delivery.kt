package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryModel (
    val id: String,
    val price: Int,
    val isWithOrder:String

)