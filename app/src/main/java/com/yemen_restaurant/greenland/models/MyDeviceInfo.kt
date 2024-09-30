package com.yemen_restaurant.greenland.models

import android.os.Build
import kotlinx.serialization.Serializable

@Serializable
data class MyDeviceInfo (
    val user_device_type:String = Build.BRAND,
    val user_device_brand:String = Build.DEVICE,
    val app_package_name: String = Build.ID,
    val app_version: String = Build.VERSION_CODES.BASE.toString(),
    )