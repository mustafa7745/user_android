package com.yemen_restaurant.greenland.storage

import GetStorage

class ReviewStorage {
    private val getStorage = GetStorage("review")
    private val isReview = "isReview"
    private val orderCount = "orderCount"

    fun isReview():Boolean{
       return getStorage.getData(isReview).isNotEmpty()
    }
    fun setReview(){
        getStorage.setData(isReview,"yes")
    }

    fun incrementCountOrder():String{
        var count = getStorage.getData(orderCount)
        if (count.isEmpty()){
            count = "1"
        }
        else{
            var countInt = count.toInt()
            countInt++;
            count = countInt.toString();
        }

        getStorage.setData(orderCount,count)
        return count
    }
    fun getCountOrder():Int{
        var count = getStorage.getData(orderCount)
        if (count.isEmpty()){
            count = "1"
        }
        else{
            var countInt = count.toInt()
            countInt++;
            count = countInt.toString();
        }
        return count.toInt()
    }
}