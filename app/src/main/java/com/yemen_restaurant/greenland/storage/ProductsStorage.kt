package com.yemen_restaurant.greenland.storage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.yemen_restaurant.greenland.models.ProductImageModel
import com.yemen_restaurant.greenland.models.ProductModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ProductsStorage(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val currenctDate: LocalDateTime = LocalDateTime.now()
//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")


    // Get the current timestamp
//    val currentTimestamp = LocalDateTime.now().format(formatter)

    companion object {
        private const val DATABASE_NAME = "products.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PRODUCTS = "products"
        private const val TABLE_PRODUCT_IMAGES = "product_images"

        // Product column names
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRE_PRICE = "prePrice"
        const val COLUMN_POST_PRICE = "postPrice"
        const val COLUMN_CATEGORY_ID = "categoryId"
        const val COLUMN_IS_AVAILABLE = "isAvailable"
        const val COLUMN_GROUP_ID = "products_groupsId"
        const val COLUMN_GROUP_NAME = "products_groupsName"
        const val COLUMN_CREATED_AT = "createdAt"
        const val COLUMN_UPDATED_AT = "updatedAt"

        // Image column names
        const val COLUMN_IMAGE_ID = "image_id"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createProductsTable = ("CREATE TABLE $TABLE_PRODUCTS ("
                + "$COLUMN_ID TEXT PRIMARY KEY,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_PRE_PRICE TEXT,"
                + "$COLUMN_POST_PRICE TEXT,"
                + "$COLUMN_CATEGORY_ID TEXT,"
                + "$COLUMN_IS_AVAILABLE TEXT,"
                + "$COLUMN_GROUP_ID TEXT,"
                + "$COLUMN_GROUP_NAME TEXT,"
                + "$COLUMN_CREATED_AT TEXT,"
                + "$COLUMN_UPDATED_AT TEXT)")
        db.execSQL(createProductsTable)

        val createProductImagesTable = ("CREATE TABLE $TABLE_PRODUCT_IMAGES ("
                + "$COLUMN_IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_PRODUCT_ID TEXT,"
                + "$COLUMN_IMAGE_URL TEXT,"
                + "FOREIGN KEY($COLUMN_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_ID))")
        db.execSQL(createProductImagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT_IMAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    // Function to add a product
    fun addProduct(product: ProductModel): Long {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID, product.id)
        values.put(COLUMN_NAME, product.name)
        values.put(COLUMN_PRE_PRICE, product.prePrice)
        values.put(COLUMN_POST_PRICE, product.postPrice)
        values.put(COLUMN_CATEGORY_ID, product.categoryId)
        values.put(COLUMN_IS_AVAILABLE, product.isAvailable)
        values.put(COLUMN_GROUP_ID, product.products_groupsId)
        values.put(COLUMN_GROUP_NAME, product.products_groupsName)
        values.put(COLUMN_CREATED_AT, currenctDate.toString())

        // Insert product and get its ID
        val productId = db.insert(TABLE_PRODUCTS, null, values)

        // Add product images
        product.productImages.forEach { image ->
            Log.e("immmmage",image.toString())
            addProductImage(product.id, image.image)
        }

//        db.close()
        Log.e("resss0",productId.toString())
        return productId
    }

    // Function to add a product image
    private fun addProductImage(productId: String, imageUrl:String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, productId)
        values.put(COLUMN_IMAGE_URL, imageUrl) // Assuming ProductImageModel has an imageUrl property

        val result = db.insert(TABLE_PRODUCT_IMAGES, null, values)
        Log.e("done",productId + " "+ imageUrl,)
//        db.close()
        Log.e("resss1",result.toString())
        return result
    }

    // Function to get products by category ID
    @SuppressLint("Range")
    fun getProductsByCategoryId(categoryId: String): List<ProductModel> {
        val productList = mutableListOf<ProductModel>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_PRODUCTS, null, "$COLUMN_CATEGORY_ID=?", arrayOf(categoryId), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val productId = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
                val productImages = getProductImages(productId)

                val product = ProductModel(
                    id = productId,
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    prePrice = cursor.getString(cursor.getColumnIndex(COLUMN_PRE_PRICE)),
                    postPrice = cursor.getString(cursor.getColumnIndex(COLUMN_POST_PRICE)),
                    categoryId = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                    isAvailable = cursor.getString(cursor.getColumnIndex(COLUMN_IS_AVAILABLE)),
                    products_groupsId = cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_ID)),
                    products_groupsName = cursor.getString(cursor.getColumnIndex(COLUMN_GROUP_NAME)),
                    productImages = productImages
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
//        db.close()
        return productList
    }

    // Function to get product images by product ID
    @SuppressLint("Range")
    private fun getProductImages(productId: String): List<ProductImageModel> {
        Log.e("start","start")
        val images = mutableListOf<ProductImageModel>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_PRODUCT_IMAGES, null, "$COLUMN_PRODUCT_ID=?", arrayOf(productId), null, null, null)
        Log.e("cursor",cursor.count.toString())
        if (cursor.moveToFirst()) {
            Log.e("first","1")
            do {
                val imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL))
                Log.e("dddd",imageUrl)
                images.add(ProductImageModel(imageUrl)) // Assuming ProductImageModel has an imageUrl property
            } while (cursor.moveToNext())
        }
        cursor.close()
//        db.close()
        return images
    }

    // Function to delete products by category ID
    fun deleteProductsByCategoryId(categoryId: String): Int {
        val db = this.writableDatabase
        val productIds = getProductsByCategoryId(categoryId).map { it.id }

        // Delete product images first
        productIds.forEach { productId ->
            db.delete(TABLE_PRODUCT_IMAGES, "$COLUMN_PRODUCT_ID=?", arrayOf(productId))
        }

        // Now delete products
        val result = db.delete(TABLE_PRODUCTS, "$COLUMN_CATEGORY_ID=?", arrayOf(categoryId))
//        db.close()
        return result
    }

    // Function to get creation times for products by category ID
    @SuppressLint("Range")
    fun getTimeWhenStoredByCategoryId(categoryId: String): List<String> {
        val times = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(TABLE_PRODUCTS, arrayOf(COLUMN_CREATED_AT), "$COLUMN_CATEGORY_ID=?", arrayOf(categoryId), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                times.add(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)))
            } while (cursor.moveToNext())
        }
        cursor.close()
//        db.close()
        return times
    }
    fun getLocalDateTime(): LocalDateTime? {
        // Define the expected format of the date-time string, including milliseconds
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

        return try {

            val s =  LocalDateTime.parse(currenctDate.toString(), formatter)
            Log.e("dfrf",s.toString())
            s

        } catch (e: DateTimeParseException) {
            println("Error parsing date: ${e.message}")
            null // Return null in case of a parsing error
        }
    }

}

//
//class ProductsStorage(val categoryId:String) {
//    private val getStorage = GetStorage("products")
//    private val homeComponentKey = "homeComponentKey"
//    private val dateKey = "dateKey"
//
//    fun isSetHomeComponent():Boolean{
//        return getStorage.getData(homeComponentKey).isNotEmpty()
//    }
//    fun setHomeComponent(data:String){
//        val currentDate: LocalDateTime = LocalDateTime.now()
//        getStorage.setData(dateKey,currentDate.toString())
//        getStorage.setData(homeComponentKey,data)
//    }
//
//    fun getDate(): LocalDateTime? {
//        return (LocalDateTime.parse(getStorage.getData(dateKey)))
//    }
//    fun getHomeComponent(): HomeComponent {
//        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(homeComponentKey))
//    }
//}