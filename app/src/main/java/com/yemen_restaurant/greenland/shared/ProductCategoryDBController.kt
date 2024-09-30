package com.yemen_restaurant.greenland.shared

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.yemen_restaurant.greenland.models.CategoryModel
import com.yemen_restaurant.greenland.models.ProductModel
import kotlinx.serialization.encodeToString

object ProductsContract{
    object ProductEntry :BaseColumns{
        const val TABLE_NAME = "products"
        const val COLUMN_NAME_ID ="category_id"
        const val COLUMN_NAME_DATA = "products"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE products (" +
            "category_id TEXT PRIMARY KEY , products TEXT)"
private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS products"

class ProductCategoryDBController(context: Context):SQLiteOpenHelper(context,"store",null,1) {


    val c_time = "c_tiem"
    fun setCategoryTime(date:String){
        val db = writableDatabase
        db.delete("time","time_id = ?", arrayOf(c_time))
        val values = ContentValues().apply {
            put("time_id",c_time)
            put("time_value",date)
        }
        db.insert("time",null,values)
    }
    fun getCategoryTime():String{
        val db = readableDatabase
        val curser = db.query("time", arrayOf("time_value"),"time_id = ?",
            arrayOf(c_time),null,null,null)

        with (curser){
            while (moveToNext()){
                val item = getString(getColumnIndexOrThrow("time_value"))
                if (item != null){
                    return  item
                }
            }
        }
        return  ""
    }
    fun isSetCategoryTime(): Boolean {
        return getCategoryTime().isNotEmpty()
    }

    fun setCategories(data:List<CategoryModel>){
        val db = writableDatabase
        db.delete("categories","category_id = ?", arrayOf(c_time))
        val values = ContentValues().apply {
            put("category_id",c_time)
            put("categories", MyJson.MyJson.encodeToString(data))
        }
        db.insert("categories",null,values)
    }

    fun getCategories(): List<CategoryModel> {
        val db = readableDatabase
        val curser = db.query("categories", arrayOf("categories"),"category_id = ?",
            arrayOf(c_time),null,null,null)

        val items = arrayListOf<CategoryModel>()
        with (curser){
            while (moveToNext()){
                val item = getString(getColumnIndexOrThrow("categories"))
                if (item != null){
                    return  MyJson.MyJson.decodeFromString(item)
                }
            }
        }
        return  listOf()
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
        val db = writableDatabase
        db.delete("time","time_id = ?", arrayOf(key))
        val values = ContentValues().apply {
            put("time_id",key)
            put("time_value",date)
        }
        db.insert("time",null,values)
    }
    fun getProductTime(key: String):String{
        val db = readableDatabase
        val curser = db.query("time", arrayOf("time_value"),"time_id = ?",
            arrayOf(key),null,null,null)

        with (curser){
            while (moveToNext()){
                val item = getString(getColumnIndexOrThrow("time_value"))
                if (item != null){
                    return  item
                }
            }
        }
        return  ""
    }
    fun isSetProductTime(key: String): Boolean {
        Log.e("frfr",getProductTime(key))
        return getProductTime(key).isNotEmpty()
    }

    fun setProducts(key: String,data:List<ProductModel>){
        val db = writableDatabase
        db.delete("products","category_id = ?", arrayOf(key))
        val values = ContentValues().apply {
            put("category_id",key)
            put("products", MyJson.MyJson.encodeToString(data))
        }
        db.insert("products",null,values)
    }

    fun getProducts(key: String): List<ProductModel> {
        val db = readableDatabase
        val curser = db.query("products", arrayOf("products"),"category_id = ?",
            arrayOf(key),null,null,null)

        val items = arrayListOf<ProductModel>()
        with (curser){
            while (moveToNext()){
                val item = getString(getColumnIndexOrThrow("products"))
                if (item != null){
                    return  MyJson.MyJson.decodeFromString(item)
                }
            }
        }
        return  listOf()
    }
    fun getProductsIds(key: String): ArrayList<String> {
        val ids = arrayListOf<String>()
        getProducts(key).forEach {
            ids.add((it.id))
        }
        return ids
    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(SQL_CREATE_ENTRIES)
        p0.execSQL("CREATE TABLE time (time_id TEXT PRIMARY KEY , time_value TEXT)")
        p0.execSQL("CREATE TABLE categories (category_id TEXT PRIMARY KEY , categories TEXT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}