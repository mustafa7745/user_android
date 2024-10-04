package com.yemen_restaurant.greenland.shared

import GetStorage
import com.yemen_restaurant.greenland.models.SuccessModel

class Login {
    private val inventory = "login"
    private val getStorage = GetStorage(inventory);
    private val token = "login_token"
    private val projectId = "p_id"
    private val serverKey = "s_k"

    fun setLoginToken(data:String){
        if (getLoginToken()!=data)
            getStorage.setData(token, data)
    }
    fun getLoginToken():String{
        return getStorage.getData(token)
    }
    fun getLoginTokenWithDate(): SuccessModel {
        return MyJson.IgnoreUnknownKeys.decodeFromString(getLoginToken())
    }

    fun isSetLoginToken(): Boolean {
        return getLoginToken().isNotEmpty()
    }
//
    fun getProjectId():String{
        return getStorage.getData(projectId)
    }
    fun setProjectId(data:String){
        if (getProjectId()!=data)
            getStorage.setData(projectId, data)
    }
    fun isSetProjectId(): Boolean {
        return getProjectId().isNotEmpty()
    }
    //
    fun getServerKey():String{
        return getStorage.getData(serverKey)
    }
    fun setServerKey(data:String){
        if (getProjectId()!=data)
            getStorage.setData(serverKey, data)
    }
    fun isSetServerKey(): Boolean {
        return getServerKey().isNotEmpty()
    }


}