package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectAdsModel (
    val project_ads_id: String,
    val project_ads_description: String,
    val project_id: String,
    val project_ads_image: String,
    val project_ads_created_at: String,
    val project_ads_updated_at: String,
    val project_ads_expire_at: String
)