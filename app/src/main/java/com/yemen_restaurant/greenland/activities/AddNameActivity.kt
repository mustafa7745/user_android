package com.yemen_restaurant.greenland.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yemen_restaurant.greenland.MainCompose2
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody

class AddNameActivity : ComponentActivity() {
    val stateController = StateController()
    private val requestServer = RequestServer(this)
    val name = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            GreenlandRestaurantTheme{
                MainCompose2(padding = 0.dp, stateController =stateController , activity = this@AddNameActivity ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "أخبرنا عن اسمك عند استخدامك التطبيق",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EE)
                            )
                        )
                        var isValidName by remember { mutableStateOf(true) }
                        if (!isValidName) {
                            Text(
                                text = "يحب الايزيد طول الاسم اكثر من 100 حرف",
                                color = Color.Red,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        OutlinedTextField(
                            label = { Text("الاسم") },
                            modifier = Modifier.fillMaxWidth(),
                            value =name.value , onValueChange = {
                                name.value = it
                            })
                        Button(
                            enabled =  isValidName,
                            onClick = { add() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "حفظ", color = Color.White, fontSize = 18.sp,fontFamily = FontFamily(
                                Font(R.font.bukra_bold)
                            )
                            )
                        }

                    }
                }
            }
        }
    }
    private fun add() {
        stateController.startAud()
        val data3 = buildJsonObject {
            put("tag", "updateName")
            put("inputUserName", name.value)
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.usersUrl, { code, it ->

            stateController.errorStateAUD(it)
        }) {
                runOnUiThread {
                    Toast.makeText(this,"تمت الاضافه بنجاح", Toast.LENGTH_SHORT).show()
                }
                stateController.successStateAUD()
                val data1 = Intent()
                data1.putExtra("user2",it)
                setResult(RESULT_OK,data1)
                finish()

        }
    }
}