package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable


@Serializable
data class SuccessModel(
    val token:String , val expire_at:String)