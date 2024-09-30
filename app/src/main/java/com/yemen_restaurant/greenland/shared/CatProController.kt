package com.yemen_restaurant.greenland.shared

import GetStorage
import android.util.Log
import com.yemen_restaurant.greenland.models.CategoryModel
import com.yemen_restaurant.greenland.models.ProductModel
import kotlinx.serialization.encodeToString

class CatProController {
    private val inventory = "cat_pro"
    private val getStorage = GetStorage(inventory);
    private val category = "cat"
    private val categoryTime = "cat_t"

    fun setCategoryTime(date:String){
        getStorage.setData(categoryTime, date)
    }
    fun getCategoryTime():String{
        return getStorage.getData(categoryTime)
    }
    fun isSetCategoryTime(): Boolean {
        return getCategoryTime().isNotEmpty()
    }

    fun setCategories(data:List<CategoryModel>){
        getStorage.setData(category, MyJson.MyJson.encodeToString(data))
    }

    fun getCategories(): List<CategoryModel> {
        return  MyJson.MyJson.decodeFromString(getStorage.getData(category))
    }
    fun getCategoriesIds(): ArrayList<String> {
        val ids = arrayListOf<String>()
        getCategories().forEach {
            ids.add((it.id))
        }
        return ids
    }
    //
    fun setProductTime(key: String,date:String){
//        Log.e("kkk123",date)
//        Log.e("kkk123",key+"r")
        val k = key + "r"
        getStorage.setData(k, date)
    }
    fun getProductTime(key: String):String{
        val k = key + "r"
        return getStorage.getData(k)
    }
    fun isSetProductTime(key: String): Boolean {
        Log.e("kkk",getProductTime(key+"r"))
        Log.e("kkk",(key+"r"))
        val k = key + "r"
        return getProductTime(k).isNotEmpty()
    }

    fun setProducts(key: String,data:List<ProductModel>){
        getStorage.setData(key, MyJson.MyJson.encodeToString(data))
    }

    fun getProducts(key: String): List<ProductModel> {
        return  MyJson.MyJson.decodeFromString(getStorage.getData(key))
    }
    fun getProductsIds(key: String): ArrayList<String> {
        val ids = arrayListOf<String>()
        getProducts(key).forEach {
            ids.add((it.id))
        }
        return ids
    }

}