package com.yemen_restaurant.greenland.shared

import GetStorage
import com.yemen_restaurant.greenland.models.CurrencyModel

class CurrencyController {
    private val inventory = "currency"
    private val getStorage = GetStorage(inventory);
    private val currency = "c"

    fun setCurrency(data:String){
            getStorage.setData(currency, data)
    }
    fun getCurrency():String{
        return getStorage.getData(currency)
    }
    fun getCurrencyModel(): CurrencyModel {
        return MyJson.MyJson.decodeFromString(getCurrency())
    }

    fun isSetCurrency(): Boolean {
        return getCurrency().isNotEmpty()
    }
//


}