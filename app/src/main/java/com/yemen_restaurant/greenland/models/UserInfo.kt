package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoModel (
    val user_id: String,
    val user_name: String,
    val user_phone: String,
    val user_password: String,
    val user_2fa_password: String,
    val user_created_at: String,
    val user_updated_at: String
)