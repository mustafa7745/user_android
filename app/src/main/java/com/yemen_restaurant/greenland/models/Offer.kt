package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OfferModel (
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val price: String,
    val expireAt: String,
    val createdAt: String,
    val updatedAt: String
)