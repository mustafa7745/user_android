package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductImageModel (
    val image: String,
//    val createdAt: String
)

class Converters {
//    @TypeConverter
//    fun fromProductImageModelList(value: List<ProductImageModel>): String {
//        return value.joinToString(";") { "${it.image},${it.createdAt}" }
//    }

//    @TypeConverter
//    fun toProductImageModelList(value: String): List<ProductImageModel> {
//        return value.split(";").map {
//            val parts = it.split(",")
//            ProductImageModel(parts[0], parts[1])
//        }
//    }
}