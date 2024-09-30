package com.yemen_restaurant.greenland.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class ProductModel (
    @PrimaryKey val id: String,
    val name: String,
    val prePrice: String,
    val postPrice: String,
    val categoryId: String,
    val isAvailable:String,
    val products_groupsId:String,
    val products_groupsName:String,
//    val createdAt: String,
//    val updatedAt: String,
    val productImages: List<ProductImageModel>

)

@Serializable
data class ProductUDModel (
    val updated: List<ProductModel>,
    val deleted: List<String>
)