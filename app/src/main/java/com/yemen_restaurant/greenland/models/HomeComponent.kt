package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class HomeComponent(
    var user: User?,
    val ads:List<AdsModel>,
    val discounts:List<ProductModel>,
    var offers:List<OfferModel>,
    val categories:List<CategoryModel>
)