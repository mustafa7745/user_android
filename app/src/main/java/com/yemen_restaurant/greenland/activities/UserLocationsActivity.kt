package com.yemen_restaurant.greenland.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yemen_restaurant.greenland.MainCompose1
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


class UserLocationsActivity :
    ComponentActivity() {
    val userStorage = UserStorage()
    val userLocations = mutableStateOf<List<UserLocationModel>>(listOf())
    val stateController = StateController()
    val requestServer = RequestServer(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        read()
        setContent {
            GreenlandRestaurantTheme {
                MainCompose1(padding = 0.dp, stateController = stateController, activity = this, read = { read() }) {
                    Button(
                        onClick = {
                            val intent = Intent(
                                this@UserLocationsActivity,
                                AddLocationActivity::class.java
                            )
                            activityResultLauncher.launch(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = if (userLocations.value.isEmpty()) "اضافة موقع" else "اضافة موقع اخر" , fontFamily = FontFamily(
                            Font(R.font.bukra_bold)
                        )
                        )

                    }

                    LazyVerticalGrid(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                        columns = GridCells.Fixed(1),
                        content = {
                            itemsIndexed(userLocations.value) { index, s ->
                               LocationCard(s = s)
                            }
                        })
                }
            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("result Avt", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val location = data.getStringExtra("location")
                if (location != null) {
                    userLocations.value =
                        listOf(MyJson.IgnoreUnknownKeys.decodeFromString<UserLocationModel>(
                            location
                        ) ) + userLocations.value
                }
            }
        } else {

        }
    }

    @Composable
    fun LocationCard(s: UserLocationModel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                LocationRow(label = "شارع", value = s.street)
                Spacer(modifier = Modifier.height(8.dp))
                LocationRow(label = "معلومات اخرى", value = s.nearTo)
                Spacer(modifier = Modifier.height(8.dp))
                LocationRow(label = "رقم التواصل", value = s.contactPhone)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val dataString = MyJson.MyJson.encodeToString(s)
                        val data1 = Intent().apply {
                            putExtra("location", dataString)
                        }
                        userStorage.setUserLocation(dataString)
//                        GetStorage("user").setData("location", dataString)
                        setResult(RESULT_OK, data1)
                        finish()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "اختر", color = Color.White,fontFamily = FontFamily(
                        Font(R.font.bukra_bold)))
                }
            }
        }
    }

    @Composable
    fun LocationRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp)
            Text(text = value, fontSize = 12.sp)
        }
    }

    private fun read() {
        stateController.isLoadingRead.value = true
        var data3 =   buildJsonObject {
            put("tag", "read")
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.userLocationUrl,{ code, it->
            stateController.errorStateRead(it)
        }){
            userLocations.value =
                MyJson.IgnoreUnknownKeys.decodeFromString<List<UserLocationModel>>(
                    it
                )
            stateController.successState()

        }
    }
}



