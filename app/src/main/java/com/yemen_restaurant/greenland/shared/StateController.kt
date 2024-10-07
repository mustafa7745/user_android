package com.yemen_restaurant.greenland.shared
import androidx.compose.runtime.mutableStateOf

class StateController {
    val isLoadingRead = mutableStateOf(false)
    val isSuccessRead = mutableStateOf(false)
    val isErrorRead = mutableStateOf(false)
    val errorRead = mutableStateOf("")
    //
    val isLoadingAUD = mutableStateOf(false)
    val isErrorAUD = mutableStateOf(false)
    val errorAUD = mutableStateOf("")

    fun startRead() {
        isErrorRead.value = false
       errorRead.value = ""
       isLoadingRead.value = true
    }

     fun errorStateRead(e:String) {
       isLoadingRead.value = false
       isErrorRead.value = true
        errorRead.value = e
    }
     fun successState() {
       isLoadingRead.value = false
         isSuccessRead.value = true
       isErrorRead.value = false
    }
    //
     fun startAud() {
        errorAUD.value = ""
       isLoadingAUD.value = true
        isErrorAUD.value = false
    }

    fun errorStateAUD(e:String) {
       isLoadingAUD.value = false
       isErrorAUD.value = true
        errorAUD.value = e
    }
     fun successStateAUD() {
        isLoadingAUD.value = false
        isErrorAUD.value = false
    }
}