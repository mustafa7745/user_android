package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.review.ReviewManagerFactory
import com.yemen_restaurant.greenland.MainCompose1
import com.yemen_restaurant.greenland.models.OrderContentModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.ReviewStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


class OrdersProductsActivity : ComponentActivity() {
    private lateinit var orderContents: OrderContentModel
    val stateController = StateController()
    private lateinit var orderId: String
    val reviewStorage = ReviewStorage()

    val requestServer = RequestServer(this)
    private fun requestInAppReview() {
        val manager = ReviewManagerFactory.create(this)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                if (reviewInfo != null) {
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // Mark that the review was requested
                        reviewStorage.setReview()
                    }
                } else {
                    // Handle the case where reviewInfo is null
                    Log.e("InAppReview", "ReviewInfo is null")
                }
            } else {
                // Handle the error case
                Log.e("InAppReview", "Failed to get review flow: ${task.exception?.message}")
            }
        }
    }


    private fun readOrderProducts() {
        stateController.isLoadingRead.value = true
        val data3 = buildJsonObject {
            put("tag", "readOrderProducts")
            put("inputOrderId", orderId)
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.ordersUrl,{ _, it->
            errorState(it)
        }){
            try {
                orderContents =
                    MyJson.IgnoreUnknownKeys.decodeFromString(
                        it
                    )
                successState()

            } catch (e: Exception) {
                errorState(e.message.toString())
            }
        }
    }

    private fun errorState(e:String) {
        stateController.isLoadingRead.value = false
        stateController.isErrorRead.value = true
        stateController.errorRead.value = e
    }

    private fun successState() {
        stateController.isLoadingRead.value = false
        stateController.isSuccessRead.value = true
        stateController.isErrorRead.value = false
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val str1 = intent.getStringExtra("orderContent")
        val str2 = intent.getStringExtra("order_id")
        if (str1 != null) {
            orderContents = MyJson.IgnoreUnknownKeys.decodeFromString(str1)
            successState()
            if (orderContents.products.isNotEmpty()) {
                orderId = orderContents.products.first().orderId
            } else if (orderContents.offers.isNotEmpty()) {
                orderId = orderContents.offers.first().orderId
            } else {
                finish()
            }
            Toast.makeText(this, "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show()
            if (!reviewStorage.isReview()){
                reviewStorage.incrementCountOrder()
                val orderCount = reviewStorage.getCountOrder()
                if (orderCount > 10){
                    requestInAppReview()
                }
            }
        } else if (str2 != null) {
            orderId = str2
            readOrderProducts()
        } else {
            finish()
        }

        setContent {
            GreenlandRestaurantTheme {
                val topBarHeight = 70.dp
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        cCompose.topBar(topBarHeight = topBarHeight, this)
                    },
                    content = {
                        MainCompose1(
                            padding = topBarHeight,
                            stateController = stateController,
                            activity = this,
                            read = { readOrderProducts()})
                           {
                                Row(
                                    Modifier.fillMaxWidth().padding(5.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment=Alignment.CenterVertically,
                                ) {
                                    Text(text = "رقم الطلب: ")
                                    Text(text = orderId)
                                }

                                // Define weights for columns

                                val column0Weight = 0.07f // 30%
                                val column1Weight = 0.44f // 30%
                                val column2Weight = 0.12f // 70%
                                val column3Weight = 0.15f // 30%
                                val column4Weight = 0.22f // 70%

                                LazyColumn(content =
                                {
                                    // Header row
                                    item {
                                        Row(Modifier.background(Color.Gray)) {
                                            TableCell(text = "#", weight = column0Weight)
                                            TableCell(text = "الصنف", weight = column1Weight)
                                            TableCell(text = "ك", weight = column2Weight)
                                            TableCell(text = "السعر", weight = column3Weight)
                                            TableCell(text = "الاجمالي", weight = column4Weight)
                                        }
                                    }

                                    itemsIndexed(orderContents.products) { index, s ->
                                        Row(Modifier.fillMaxWidth()) {
                                            TableCell(
                                                text = (index + 1).toString(),
                                                weight = column0Weight
                                            )
                                            TableCell(
                                                text = s.productName,
                                                weight = column1Weight
                                            )
                                            TableCell(
                                                text = s.productQuantity,
                                                weight = column2Weight
                                            )
                                            TableCell(
                                                text = s.productPrice,
                                                weight = column3Weight
                                            )
                                            TableCell(
                                                text = (s.productPrice.toInt() * s.productQuantity.toInt()).toString(),
                                                weight = column4Weight
                                            )
                                        }
                                    }
                                    itemsIndexed(orderContents.offers) { index, s ->
                                        Row(Modifier.fillMaxWidth()) {
                                            TableCell(
                                                text = (index + 1).toString(),
                                                weight = column0Weight
                                            )
                                            TableCell(
                                                text = s.offerName,
                                                weight = column1Weight
                                            )
                                            TableCell(
                                                text = s.offerQuantity,
                                                weight = column2Weight
                                            )
                                            TableCell(
                                                text = s.offerPrice,
                                                weight = column3Weight
                                            )
                                            TableCell(
                                                text = (s.offerPrice.toInt() * s.offerQuantity.toInt()).toString(),
                                                weight = column4Weight
                                            )
                                        }
                                    }
                                    item {

                                        if (orderContents.delivery != null)

                                            Row(Modifier.fillMaxWidth()) {
                                                TableCell(
                                                    text = (orderContents.products.size + orderContents.offers.size + 1).toString(),
                                                    weight = column0Weight
                                                )
                                                TableCell(
                                                    text = "توصيل الطلب",
                                                    weight = column1Weight
                                                )
                                                TableCell(
                                                    text = "1",
                                                    weight = column2Weight
                                                )
                                                TableCell(
                                                    text = orderContents.delivery!!.price,
                                                    weight = column3Weight
                                                )
                                                TableCell(
                                                    text = orderContents.delivery!!.price,
                                                    weight = column4Weight
                                                )

                                            }
                                        if (orderContents.discount != null) {
                                            Row(Modifier.fillMaxWidth()) {
                                                var size =
                                                    (orderContents.products.size + orderContents.offers.size + 1)
                                                val discountTypeDescription =
                                                    if (orderContents.discount!!.type == "0") {
                                                        "خصم مئوي"  // Percentage Discount
                                                    } else {
                                                        "خصم رئيسي"  // Fixed Discount
                                                    }
                                                if (orderContents.discount != null)
                                                    size++
                                                TableCell(
                                                    text = size.toString(),
                                                    weight = column0Weight
                                                )
                                                TableCell(
                                                    text = discountTypeDescription,
                                                    weight = column1Weight
                                                )
                                                TableCell(
                                                    text = "1",
                                                    weight = column2Weight
                                                )
                                                TableCell(
                                                    text = orderContents.discount!!.amount,
                                                    weight = column3Weight
                                                )
                                                TableCell(
                                                    text = orderContents.discount!!.amount,
                                                    weight = column4Weight
                                                )
                                            }

                                        }
                                        Row(Modifier.background(Color.LightGray).clickable {
                                            requestInAppReview()
                                        }) {
                                            TableCell(
                                                text = "اجمالي الفاتورة",
                                                weight = (column0Weight + column1Weight + column2Weight + column3Weight)
                                            )

                                            TableCell(
                                                text = getAllFinalPrice().toString(),
                                                weight = column4Weight
                                            )

                                        }
                                    }


                                }

                                )
                            }

                    },

                    )

            }
        }
    }

    private fun getProductsFinalPrice(): Int {
        return orderContents.products.sumOf {
            it.productPrice.toInt() * it.productQuantity.toInt()
        }
    }

    private fun getOffersFinalPrice(): Int {
        return orderContents.offers.sumOf {
            it.offerPrice.toInt() * it.offerQuantity.toInt()
        }
    }

    private fun getAllFinalPrice(): Int {
        var sum = getProductsFinalPrice() + getOffersFinalPrice()

        orderContents.delivery?.let {
            sum += it.price.toInt()
        }

        orderContents.discount?.let {
            val amount = it.amount
            when (it.type) {
                "0" -> { // Percentage discount
                    val discount = (sum * amount.toInt()) / 100
                    sum -= discount
                    sum = 50 * Math.round((sum / 50).toDouble()).toInt()
                    println("Discount: $discount")
                }

                else -> { // Fixed amount discount
                    sum -= amount.toInt()
                }
            }
        }

        return sum
    }

    @Composable
    fun RowScope.TableCell(
        text: String,
        weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
//                .size(25.dp)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}