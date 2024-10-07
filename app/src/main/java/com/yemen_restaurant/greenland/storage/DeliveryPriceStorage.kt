package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.formatPrice
import java.time.LocalDateTime

class DeliveryPriceStorage {
    private val getStorage = GetStorage("delivery")
    private val deliveryPrice = "deliveryPrice"
    private val dateKey = "dateKey"

    fun isSetDeliveryPrice():Boolean{
       return getStorage.getData(deliveryPrice).isNotEmpty()
    }
    fun setDeliveryPrice(data:String){
        val currentDate: LocalDateTime = LocalDateTime.now()
        getStorage.setData(dateKey,currentDate.toString())
        getStorage.setData(deliveryPrice,data)
    }

    fun getDate(): LocalDateTime? {
       return (LocalDateTime.parse(getStorage.getData(dateKey)))
    }
    fun getDeliveryPrice():String{
       val price = getStorage.getData(deliveryPrice);
        return formatPrice(price)
    }
}