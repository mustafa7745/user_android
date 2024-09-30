package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable


@Serializable
data class EncryptedModel(
    val encrypted_data:String)