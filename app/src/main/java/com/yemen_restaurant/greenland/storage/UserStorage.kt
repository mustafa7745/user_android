package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.User
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class UserStorage {
    val getStorage = GetStorage("user")
    val userKey = "user"
    val userLocationKey = "userLocation"
    private val dateKey = "dateKey"
    fun isSetUser():Boolean{
        return try {
            getUser()
            true
        }catch (e:Exception){
            setUser("")
            false
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
        return try {
            getUserLocation()
            true
        }catch (e:Exception){
            setUserLocation("")
            false
        }
    }
    fun setUserLocation(userLocation:String){
        val currentDate: LocalDateTime = LocalDateTime.now()
        getStorage.setData(dateKey,currentDate.toString())
        getStorage.setData(userLocationKey,userLocation)
    }
    fun getUserLocation():UserLocationModel{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(userLocationKey))
    }
    fun getDateLocation(): LocalDateTime? {
        return (LocalDateTime.parse(getStorage.getData(dateKey)))
    }
    fun setDateLocation(){
        getStorage.setData(dateKey, getCurrentDate().toString())
    }


}