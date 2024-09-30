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

}