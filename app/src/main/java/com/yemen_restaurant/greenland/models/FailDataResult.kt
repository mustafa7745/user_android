package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(val ar:String,val en:String)
@Serializable
data class ErrorMessage(val code:Int ,val message: Message)