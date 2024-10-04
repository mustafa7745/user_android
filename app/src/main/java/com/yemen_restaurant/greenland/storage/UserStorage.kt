package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.models.User
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson

class UserStorage {
    val getStorage = GetStorage("user")
    val userKey = "user"
    val userLocationKey = "userLocation"
    fun isSetUser():Boolean{
        try {
            getUser()
            return true
        }catch (e:Exception){
            setUser("")
            return false
        }
    }
    fun setUser(user:String){
        getStorage.setData(userKey,user)
    }
    fun getUser():User{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(userKey))


    }
    //
    fun isSetUserLocation():Boolean{
       return getStorage.getData(userLocationKey).isNotEmpty()
    }
    fun setUserLocation(userLocation:String){
        getStorage.setData(userLocationKey,userLocation)
    }
    fun getUserLocation():UserLocationModel{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(userLocationKey))
    }


}