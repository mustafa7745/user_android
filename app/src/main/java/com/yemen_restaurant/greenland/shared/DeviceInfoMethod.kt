package com.yemen_restaurant.greenland.shared

import GetStorage

import android.os.Build
import com.yemen_restaurant.greenland.models.MyDeviceInfo


import org.json.JSONObject

class DeviceInfoMethod  {
    private val inventory = "device_info"
    private val getStorage = GetStorage(inventory);

    fun setDeviceId(data:String){
        if (getDeviceId()!=data)
        getStorage.setData("device_id", data)
    }
    fun getDeviceId():String{
      return getStorage.getData("device_id")
    }

    fun isDeviceRegister():String{
        return getStorage.getData("register_device_status")
    }
    fun registerDevice(){
        getStorage.setData("register_device_status","yes")
    }

    fun getDeviceInfo(): JSONObject {
        val deviceInfo = MyDeviceInfo()
        val json = JSONObject()
        json.put("user_device_model",Build.MODEL)
        json.put("user_device_version",Build.VERSION.RELEASE)
        json.put("user_device_inc",Build.VERSION.INCREMENTAL)
        json.put("user_device_Manufacture",Build.BRAND)
        return json
    }

    fun setDeviceInfo(token:String){
        getStorage.setData("device_info",token)
    }



}

