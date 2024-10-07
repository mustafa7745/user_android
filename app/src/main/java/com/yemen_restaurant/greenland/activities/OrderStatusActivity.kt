package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yemen_restaurant.greenland.MainCompose1
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderStatusActivity : ComponentActivity() {
    val stateController = StateController()
    private val orderStatus = mutableStateOf<List<OrderStatus>>(listOf())
    val requestServer = RequestServer(this)
    private lateinit var orderId: String
    private fun read() {
        stateController.startRead()
        val data3 = buildJsonObject {
            put("tag", "readOrderStatus")
            put("inputOrderId", orderId)
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.ordersUrl, { _, it ->
            stateController.errorStateRead(it)
        }) {
                orderStatus.value =
                    MyJson.IgnoreUnknownKeys.decodeFromString(
                        it
                    )
               stateController.successState()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val str = intent.getStringExtra("order_id")
        if (str == null) {
            finish()
        } else {
            orderId = str
        }

        read()




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
                            read = { read() })
                        {
                            // Define weights for columns
                            val column0Weight = 0.07f // 30%
                            val column1Weight = 0.36f // 30%
                            val column2Weight = 0.27f // 70%
                            val column3Weight = 0.3f // 30%

                            LazyVerticalGrid(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(),
                                columns = GridCells.Fixed(1),
                                content = {
                                    item {
                                        Row(Modifier.background(Color.Gray)) {
                                            TableCell(text = "#", weight = column0Weight)
                                            TableCell(text = "الحاله", weight = column1Weight)
                                            TableCell(text = "الوقت", weight = column2Weight)
                                            TableCell(text = "التاريخ", weight = column3Weight)
                                        }
                                    }
                                    itemsIndexed(orderStatus.value) { index, s ->
                                        Row(Modifier.fillMaxWidth()) {
                                            TableCell(
                                                text = (index + 1).toString(),
                                                weight = column0Weight
                                            )
                                            TableCell(
                                                text = s.situation,
                                                weight = column1Weight
                                            )
                                            val formattedDateTime =
                                                s.createdAt.replace("\\s".toRegex(), "T")
                                            val date = (LocalDateTime.parse(formattedDateTime))
                                            TableCell(
                                                text = date.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                                                    .toString(),
                                                weight = column2Weight
                                            )

                                            TableCell(
                                                text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                    .toString(),
                                                weight = column3Weight
                                            )
                                        }
                                    }
                                })
                        }
                    },
                )
            }
        }
    }

    @Composable
    fun RowScope.TableCell(
        text: String,
        weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}