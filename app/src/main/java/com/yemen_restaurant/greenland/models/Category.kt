package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel (
    val id: String,
    val name: String,
    val image: String,
    val order: String,
    val category_image_path: String,
//    val createdAt: String,
//    val updatedAt: String
)
@Serializable
data class CategoryUDModel (
    val updated: List<CategoryModel>,
    val deleted: List<String>
)