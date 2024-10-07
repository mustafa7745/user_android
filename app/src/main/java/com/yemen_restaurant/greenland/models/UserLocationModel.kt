package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class UserLocationModel (
    val id: String,
    val userId: String,
    val city: String,
    val street: String,
    val latLong : String,
    val nearTo: String,
    val contactPhone: String,
    var deliveryPrice:Double,
    val createdAt: String,
    val updatedAt: String,
)