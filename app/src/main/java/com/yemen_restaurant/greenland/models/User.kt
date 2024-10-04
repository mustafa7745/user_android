package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name:String,
    var name2:String?,
    var phone:String
)