package com.yemen_restaurant.greenland.shared

import GetStorage
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AppInfoMethod {
    private val inventory = "app_info"
    private val getStorage = GetStorage(inventory);


    //PACKAGE_NAME
    fun setAppPackageName(data:String){
        if (getAppPackageName()!=data)
        getStorage.setData("app_package_name", data)
    }
    fun getAppPackageName():String{
        return getStorage.getData("app_package_name")
    }
    //VERSION
    fun setAppVersion(data:String){
        if (getAppVersion()!=data)
        getStorage.setData("app_version",data)
    }
    fun getAppVersion():String{
        return getStorage.getData("app_version")
    }
    //TOKEN
    fun getAppToken():String{

        return getStorage.getData("app_token")
    }
    @SuppressLint("SuspiciousIndentation")
    private fun setAppToken(data:String){
        if (getAppToken()!=data)
        getStorage.setData("app_token", data)
    }
    ///SHA
    fun getAppSha():String{
        return getStorage.getData("app_sha")
    }
    fun setAppSha(data:String){
        getStorage.setData("app_sha", data)
    }
    ///////
    //////
    private suspend fun getToken():Boolean{
        try {
            val res =  FirebaseMessaging.getInstance().token.await()
            if (res !=null) {
                setAppToken(res)
                return true
            }
            return false
        }
        catch (e:Exception){
            return false
        }
    }

    //////////// INIT FUNCTIONS
    suspend fun isGetToken():Boolean{
        val token = getToken()
        if (token){
            return true
        }
        return false
    }
}
class TokenVM: ViewModel(){
    fun check(onFail:()->Unit,onSuccess:()->Unit){
        GlobalScope.launch {
            val s= AppInfoMethod().isGetToken()
            if (s){
                onSuccess()
            }
            else{
              onFail()
            }
        }
    }
}
